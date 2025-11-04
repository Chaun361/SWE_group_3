package com.example.demo.Order.controller;

import com.example.demo.Cart.exception.ResourceNotFoundException;
import com.example.demo.Cart.exception.StockException;
import com.example.demo.Order.exception.ErrorResponse;
import com.example.demo.Order.model.OrderModel;
import com.example.demo.Order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> payload) {
        try {
            Object userIdObj = payload.get("userId");

            // 1. ตรวจสอบว่ามี key "userId" หรือไม่ และค่าเป็น null หรือไม่
            if (userIdObj == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("userId is required and cannot be null."));
            }

            // 2. ตรวจสอบว่าเป็นตัวเลขหรือไม่ และแปลงให้เป็น Long (วิธีที่ปลอดภัย)
            if (!(userIdObj instanceof Number)) {
                 return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Invalid userId format. Must be a numeric value."));
            }

            // แปลงจาก Number เป็น Long โดยตรง ปลอดภัยกว่าและไม่มีสีเหลือง
            Long userId = ((Number) userIdObj).longValue();


            // 3. เรียกใช้ Service
            OrderModel createdOrder = orderService.checkout(userId);
            return ResponseEntity.ok(createdOrder);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (StockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred."));
        }
    }
}