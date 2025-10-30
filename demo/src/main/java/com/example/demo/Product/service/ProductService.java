package com.example.demo.Product.service;

import org.springframework.stereotype.Service;
import com.example.demo.Product.repository.ProductRepository;
import com.example.demo.Product.model.ProductModel;
import java.util.List;


@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
// getallProducts
    public List<ProductModel> getAllProducts() {
        return productRepository.findAll();
    }
}
