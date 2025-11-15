package com.example.demo.Order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.demo.Order.model.OrderModel;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    // 🧩 ดึงรายการออเดอร์ทั้งหมดของลูกค้าตาม userId
    @Query("SELECT o FROM OrderModel o WHERE o.UserID = ?1")
    List<OrderModel> findByUserID(Long userId);
}
