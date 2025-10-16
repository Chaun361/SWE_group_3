package com.example.demo.Order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.Order.model.OrderItemModel;
@Repository

public interface OrderItemRepository extends JpaRepository<OrderItemModel, Long> {
    
}
