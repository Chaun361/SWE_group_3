package com.example.demo.Cart.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import com.example.demo.Cart.exception.StockException;

import com.example.demo.Cart.dto.CartDTO;
import com.example.demo.Cart.service.CartService;

@RestController
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/api/cart/{userId}")
    public CartDTO getActiveCartByUserId(@PathVariable Long userId) {
        return cartService.getActiveCartByUserId(userId);
    }

    @PostMapping("/api/cart/add")
    public CartDTO addProductToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        System.out.println("Received request to add product to cart: userId=" + userId + ", productId=" + productId
                + ", quantity=" + quantity);
        
        return cartService.addProductToCart(userId, productId, quantity);
    }

    @PostMapping("/api/cart/remove")
    public CartDTO removeProductFromCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return cartService.removeProductFromCart(userId, productId, quantity);
    }

    
    @ExceptionHandler(StockException.class)
    public ResponseEntity<Map<String, String>> handleStockException(StockException ex) {
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage()); 

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}