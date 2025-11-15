package com.example.demo.Cart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.Cart.dto.CartDTO;
import com.example.demo.Cart.dto.CartItemDTO;
import com.example.demo.Cart.exception.ResourceNotFoundException;
import com.example.demo.Cart.exception.StockException;
import com.example.demo.Cart.model.CartItemsModel;
import com.example.demo.Cart.model.CartModel;
import com.example.demo.Cart.repository.CartItemsRepository;
import com.example.demo.Cart.repository.CartRepository;
import com.example.demo.Product.model.ProductModel;
import com.example.demo.Product.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ProductRepository productRepository; // <-- เปลี่ยนเป็น ProductRepository

    // แก้ไข Constructor ให้รับ ProductRepository
    public CartService(CartRepository cartRepository, CartItemsRepository cartItemsRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.productRepository = productRepository; // <-- แก้ไขตรงนี้
    }

    @Transactional
    public CartDTO addProductToCart(Long userId, Long productId, int quantity) {

        // 1. ตรวจสอบ Product และ Stock โดยใช้ productRepository
        ProductModel product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        // หา CartModel ก่อนเพื่อใช้ cartId ต่อไป
        CartModel cart = cartRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseGet(() -> {
                    // If no active cart exists, create a new one
                    CartModel newCart = new CartModel();
                    newCart.setUserId(userId);
                    newCart.setActive(true);
                    return cartRepository.save(newCart);
                });

        // ดึงรายการสินค้าในตะกร้าแล้วหาว่ามีสินค้านี้อยู่หรือไม่
        List<CartItemsModel> cartItems = cartItemsRepository.findByCart_CartId(cart.getCartId());
        CartItemsModel existingItem = cartItems.stream()
                .filter(i -> productId.equals(i.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // คำนวณจำนวนใหม่และตรวจสอบสต็อก
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new StockException("Insufficient stock for product ID " + productId);
            }
            existingItem.setQuantity(newQuantity);
            cartItemsRepository.save(existingItem);
        } else {
            // ไม่มีสินค้าเดิม: ตรวจสอบสต็อกแล้วสร้างรายการใหม่
            if (product.getStockQuantity() < quantity) {
                throw new StockException("Insufficient stock for product ID " + productId);
            }
            CartItemsModel newItem = new CartItemsModel();
            newItem.setCart(cart);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cartItemsRepository.save(newItem);
        }

        // คืนค่า DTO ของตะกร้า (ฟังก์ชันจะคำนวณราคารวมจากรายการ)
        return mapCartToDTO(cart.getCartId());
    }

    // Helper Method สำหรับคำนวณราคาและแปลงเป็น DTO
    private CartDTO mapCartToDTO(Long cartId) {
        CartModel cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "ID", cartId));

        List<CartItemsModel> cartItems = cartItemsRepository.findByCart_CartId(cartId);

        List<CartItemDTO> itemDTOs = cartItems.stream()
                .map(item -> {
                    // แก้ไขตรงนี้ให้ใช้ productRepository และ ProductModel
                    ProductModel product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", item.getProductId()));
                    return CartItemDTO.fromEntity(item, product); // <-- ส่ง ProductModel เข้าไปแทน
                })
                .collect(Collectors.toList());

        // ... (ส่วนที่เหลือเหมือนเดิม)
        double totalPrice = itemDTOs.stream()
                .mapToDouble(CartItemDTO::getItemTotalPrice)
                .sum();

        return CartDTO.fromEntity(cart, itemDTOs, totalPrice);
    }

    public CartDTO getActiveCartByUserId(Long userId) {
        CartModel cart = cartRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Active Cart", "User ID", userId));

        return mapCartToDTO(cart.getCartId());
    }

    public CartDTO removeProductFromCart(Long userId, Long productId, int quantity) {
        CartModel cart = cartRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Active Cart", "User ID", userId));

        CartItemsModel cartItem = cartItemsRepository.findByCart_CartIdAndProductId(cart.getCartId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item", "Product ID", productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        if (cartItem.getQuantity() <= quantity) {
            cartItemsRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() - quantity);
            cartItemsRepository.save(cartItem);
        }

        return mapCartToDTO(cart.getCartId());
    }
       
}