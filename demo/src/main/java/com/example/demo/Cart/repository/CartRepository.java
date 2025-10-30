package com.example.demo.Cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.Cart.model.CartModel;
import java.util.Optional; // ต้องใช้ Optional

@Repository
public interface CartRepository extends JpaRepository<CartModel, Long> {
    
    // ถูกแก้ไขให้คืนค่าเป็น Optional ตาม Best Practice
    Optional<CartModel> findByUserIdAndIsActiveTrue(Long userId);

}