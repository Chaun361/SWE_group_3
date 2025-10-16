package com.example.demo.Cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.Cart.model.CartProductModel; 

public interface CartProductRepository extends JpaRepository<CartProductModel, Long> {

}