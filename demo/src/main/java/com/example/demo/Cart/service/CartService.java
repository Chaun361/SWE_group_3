package com.example.demo.Cart.service;

import com.example.demo.Cart.dto.CartDTO;
import com.example.demo.Cart.dto.CartItemDTO;
import com.example.demo.Cart.exception.ResourceNotFoundException;
import com.example.demo.Cart.exception.StockException;
import com.example.demo.Cart.model.CartItemsModel;
import com.example.demo.Cart.model.CartModel;
import com.example.demo.Cart.model.CartProductModel;
import com.example.demo.Cart.repository.CartItemsRepository;
import com.example.demo.Cart.repository.CartProductRepository;
import com.example.demo.Cart.repository.CartRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final CartProductRepository cartProductRepository;

    public CartService(CartRepository cartRepository, CartItemsRepository cartItemsRepository, CartProductRepository cartProductRepository) {
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.cartProductRepository = cartProductRepository;
    }

    @Transactional
    public CartDTO addProductToCart(Long userId, Long productId, int quantity) {
        
        // 1. ตรวจสอบ Product และ Stock
        CartProductModel product = cartProductRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        
        // 2. ค้นหา/สร้าง Cart ที่ Active
        CartModel cart = cartRepository.findByUserIdAndIsActiveTrue(userId)
            .orElseGet(() -> {
                CartModel newCart = new CartModel();
                newCart.setUserId(userId);
                newCart.setActive(true);
                return cartRepository.save(newCart);
            });
        
        // 3. ตรวจสอบ Cart Item เดิม
        Optional<CartItemsModel> existingItemOpt = cartItemsRepository
            .findByCart_CartIdAndProductId(cart.getCartId(), productId);

        if (existingItemOpt.isPresent()) {
            // 3a. มีสินค้าเดิม -> เพิ่ม Quantity
            CartItemsModel existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            
            // ตรวจสอบสต็อกรวม 
            if (product.getStockQuantity() < newQuantity) {
                 throw new StockException("Adding " + quantity + " exceeds available stock. Max quantity: " + product.getStockQuantity());
            }
            existingItem.setQuantity(newQuantity);
            cartItemsRepository.save(existingItem);
        } else {
            // 3b. ไม่มีสินค้าเดิม -> เพิ่ม Cart Item ใหม่
            if (product.getStockQuantity() < quantity) { // ตรวจสอบสต็อกสำหรับสินค้าใหม่
                 throw new StockException("Not enough stock for product " + productId + ". Available: " + product.getStockQuantity());
            }
            CartItemsModel newItem = new CartItemsModel();
            newItem.setCart(cart);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cartItemsRepository.save(newItem);
        }

        // 4. คำนวณราคารวมและสร้าง DTO สำหรับ Response
        return mapCartToDTO(cart.getCartId());
    }

    // Helper Method สำหรับคำนวณราคาและแปลงเป็น DTO
    private CartDTO mapCartToDTO(Long cartId) {
        CartModel cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart", "ID", cartId));

        List<CartItemsModel> cartItems = cartItemsRepository.findByCart_CartId(cartId);
        
        List<CartItemDTO> itemDTOs = cartItems.stream()
            .map(item -> {
                CartProductModel product = cartProductRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", item.getProductId()));
                return CartItemDTO.fromEntity(item, product);
            })
            .collect(Collectors.toList());

        // คำนวณราคารวมทั้งหมดจาก DTOs ที่คำนวณแล้ว
        double totalPrice = itemDTOs.stream()
            .mapToDouble(CartItemDTO::getItemTotalPrice)
            .sum();
        
        return CartDTO.fromEntity(cart, itemDTOs, totalPrice);
    }
    
    // เมธอด findActiveCartByUserId
    public CartDTO findActiveCartByUserId(Long userId) {
        CartModel activeCart = cartRepository.findByUserIdAndIsActiveTrue(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Active Cart", "User ID", userId));
        
        return mapCartToDTO(activeCart.getCartId());
    }
}