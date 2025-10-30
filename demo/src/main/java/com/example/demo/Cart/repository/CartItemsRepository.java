package com.example.demo.Cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Cart.model.CartItemsModel;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItemsModel, Long> {

    Optional<CartItemsModel> findByCart_CartIdAndProductId(Long cartId, Long productId);

    List<CartItemsModel> findByCart_CartId(Long cartId); 
}