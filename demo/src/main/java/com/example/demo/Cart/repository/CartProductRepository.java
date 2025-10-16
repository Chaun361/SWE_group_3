package com.example.demo.Cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.Cart.model.CartProductModel; // ใช้ CartProductModel

@Repository
public interface CartProductRepository extends JpaRepository<CartProductModel, Long> {

}