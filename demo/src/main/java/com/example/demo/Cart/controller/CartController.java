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
@RequestMapping("/api/cart") // แนะนำให้กำหนด base path ไว้ที่นี่
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Endpoint สำหรับดึงข้อมูลตะกร้าของผู้ใช้
     * แก้ไขให้เรียกใช้ cartService.getCartByUserId และใช้ ResponseEntity
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable Long userId) {
        // เรียกใช้ service เพื่อดึงข้อมูลจริง
        CartDTO cart = cartService.getCartByUserId(userId); 
        // ส่ง status 200 OK กลับไปพร้อมข้อมูลตะกร้า
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