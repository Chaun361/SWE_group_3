package com.example.demo.Order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Order.model.OrderModel;

@Repository

public interface OrderRepository extends JpaRepository<OrderModel, Long> {

}
