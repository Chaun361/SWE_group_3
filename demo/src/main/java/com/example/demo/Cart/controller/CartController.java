package com.example.demo.Cart.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Cart.dto.CartDTO;
import com.example.demo.Cart.service.CartService;


@RestController
public class CartController {

    // แก้ไข Naming: ใช้ Camel Case สำหรับ field
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/api/cart/{userId}")
    public CartDTO getActiveCartByUserId(@PathVariable Long userId) {
        return cartService.findActiveCartByUserId(userId);
    }
    
    @PostMapping("/api/cart/add")
    public CartDTO addProductToCart(
        @RequestParam Long userId,
        @RequestParam Long productId,
        @RequestParam int quantity
    ) {
        return cartService.addProductToCart(userId, productId, quantity);
    }

}