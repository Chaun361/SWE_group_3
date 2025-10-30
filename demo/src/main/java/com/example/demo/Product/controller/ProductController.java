package com.example.demo.Product.controller;

import org.springframework.web.bind.annotation.RestController;
import com.example.demo.Product.model.ProductModel;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.Product.service.ProductService;



@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/api/products")
    public List<ProductModel> getAllProducts() {
        return productService.getAllProducts();
    }
    
}
