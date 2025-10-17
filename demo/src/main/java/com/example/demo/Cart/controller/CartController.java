package com.example.demo.Cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Cart.dto.CartDTO;
import com.example.demo.Cart.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Endpoint สำหรับดึงข้อมูลตะกร้าของผู้ใช้
     * ถ้าสำเร็จจะคืนค่า 200 OK พร้อมข้อมูล
     * ถ้าตะกร้าว่างจะคืนค่า 204 No Content
     * ถ้าไม่พบตะกร้าจะคืนค่า 404 Not Found
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable Long userId) {
        CartDTO cart = cartService.findActiveCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }
    
    /**
     * Endpoint สำหรับเพิ่มสินค้าลงตะกร้า
     */
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addProductToCart(
        @RequestParam Long userId,
        @RequestParam Long productId,
        @RequestParam int quantity
    ) {
        CartDTO updatedCart = cartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }
}