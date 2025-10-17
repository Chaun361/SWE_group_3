package com.example.demo.Cart.service;

import com.example.demo.Cart.dto.CartDTO;
import com.example.demo.Cart.dto.CartItemDTO;
import com.example.demo.Cart.exception.CartEmptyException;
import com.example.demo.Cart.exception.ResourceNotFoundException;
import com.example.demo.Cart.exception.StockException;
import com.example.demo.Cart.model.CartItemsModel;
import com.example.demo.Cart.model.CartModel;
import com.example.demo.Cart.repository.CartItemsRepository;
import com.example.demo.Cart.repository.CartRepository;
import com.example.demo.Product.model.ProductModel;
import com.example.demo.Product.repository.ProductRepository;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemsRepository cartItemsRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.productRepository = productRepository;
    }

    /**
     * เพิ่มสินค้าลงในตะกร้า หรืออัปเดตจำนวนถ้ามีอยู่แล้ว
     */
    @Transactional
    public CartDTO addProductToCart(Long userId, Long productId, int quantity) {

        ProductModel product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        CartModel cart = cartRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "ID", userId));

        CartItemsModel existingItem = cartItemsRepository.findByCart_CartIdAndProductId(cart.getCartId(), productId)
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new StockException("Insufficient stock for product ID " + productId);
            }
            existingItem.setQuantity(newQuantity);
            cartItemsRepository.save(existingItem);
        } else {
            if (product.getStockQuantity() < quantity) {
                throw new StockException("Insufficient stock for product ID " + productId);
            }
            CartItemsModel newItem = new CartItemsModel();
            newItem.setCart(cart);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cartItemsRepository.save(newItem);
        }

        return mapCartToDTO(cart.getCartId());
    }

    /**
     * ค้นหาตะกร้าที่ใช้งานของผู้ใช้
     * @param userId ID ของผู้ใช้
     * @return CartDTO ที่มีข้อมูลสินค้า
     * @throws ResourceNotFoundException หากไม่พบตะกร้า
     * @throws CartEmptyException หากตะกร้าว่างเปล่า (ไม่มีสินค้า)
     */
    public CartDTO findActiveCartByUserId(Long userId) {
        CartModel cart = cartRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "ID", userId));

        List<CartItemsModel> cartItems = cartItemsRepository.findByCart_CartId(cart.getCartId());

        if (cartItems.isEmpty()) {
            // โยน Exception เมื่อตะกร้าว่างเปล่า
            throw new CartEmptyException("Cart for user ID " + userId + " is empty.");
        }

        return mapCartToDTO(cart.getCartId());
    }

    /**
     * Helper Method สำหรับแปลงข้อมูลจาก Entity เป็น DTO และคำนวณราคารวม
     */
    private CartDTO mapCartToDTO(Long cartId) {
        CartModel cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "ID", cartId));

        List<CartItemsModel> cartItems = cartItemsRepository.findByCart_CartId(cartId);

        List<CartItemDTO> itemDTOs = cartItems.stream()
                .map(item -> {
                    ProductModel product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", item.getProductId()));
                    return CartItemDTO.fromEntity(item, product);
                })
                .collect(Collectors.toList());

        double totalPrice = itemDTOs.stream()
                .mapToDouble(CartItemDTO::getItemTotalPrice)
                .sum();

        return CartDTO.fromEntity(cart, itemDTOs, totalPrice);
    }
}