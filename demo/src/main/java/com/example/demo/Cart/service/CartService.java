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

        // 1. ตรวจสอบ Product และ Stock
        ProductModel product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        // 2. หา Cart ของ User (ถ้าไม่มีก็สร้างใหม่) - อาจปรับแก้ตาม business logic
        CartModel cart = cartRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "ID", userId));

        // 3. ค้นหาสินค้าเดิมในตะกร้า
        CartItemsModel existingItem = cartItemsRepository.findByCart_CartIdAndProductId(cart.getCartId(), productId)
                .orElse(null);

        if (existingItem != null) {
            // มีสินค้าอยู่แล้ว: อัปเดตจำนวน
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new StockException("Insufficient stock for product ID " + productId);
            }
            existingItem.setQuantity(newQuantity);
            cartItemsRepository.save(existingItem);
        } else {
            // ไม่มีสินค้าเดิม: สร้างรายการใหม่
            if (product.getStockQuantity() < quantity) {
                throw new StockException("Insufficient stock for product ID " + productId);
            }
            CartItemsModel newItem = new CartItemsModel();
            newItem.setCart(cart);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cartItemsRepository.save(newItem);
        }

        // 4. คืนค่า DTO ของตะกร้าล่าสุด
        return mapCartToDTO(cart.getCartId());
    }

    /**
     * ดึงข้อมูลตะกร้าสินค้าของผู้ใช้ พร้อมตรวจสอบว่ามีสินค้าในตะกร้าหรือไม่
     * @param userId ID ของผู้ใช้
     * @return CartDTO ที่มีข้อมูลสินค้าและราคารวม
     * @throws ResourceNotFoundException หากไม่พบตะกร้าของผู้ใช้นี้
     * @throws CartEmptyException หากตะกร้าว่างเปล่า
     */
    public CartDTO getCartByUserId(Long userId) {
        // 1. ค้นหาตะกร้าของผู้ใช้
        CartModel cart = cartRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "ID", userId));

        // 2. ดึงรายการสินค้าทั้งหมดในตะกร้า
        List<CartItemsModel> cartItems = cartItemsRepository.findByCart_CartId(cart.getCartId());

        // 3. ตรวจสอบว่ารายการสินค้าว่างเปล่าหรือไม่
        if (cartItems.isEmpty()) {
            // ถ้าว่าง ให้โยน CartEmptyException
            throw new CartEmptyException("Cart for user ID " + userId + " is empty.");
        }

        // 4. ถ้ามีสินค้า ให้ทำการคำนวณและแปลงเป็น DTO
        return mapCartToDTO(cart.getCartId());
    }


    /**
     * Helper Method สำหรับแปลงข้อมูลจาก Entity เป็น DTO และคำนวณราคารวม
     * ถูกเรียกใช้ภายใน Service นี้เท่านั้น
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