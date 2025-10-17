package com.example.demo.Cart.exception; // หรือ package หลักของคุณ

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Class นี้จะดักจับ Exception ที่เกิดขึ้นใน Controller ทั้งหมด
 * และแปลงเป็น ResponseEntity ที่มี HTTP Status และ Body ที่เหมาะสม
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * จัดการ ResourceNotFoundException และ CartEmptyException
     * ทั้งสองกรณีนี้ควรส่งกลับเป็น HTTP 404 Not Found
     */
    @ExceptionHandler({ResourceNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); // นำ message จาก Exception มาแสดง

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * จัดการ StockException และ IllegalArgumentException
     * กรณีเหล่านี้เป็นข้อผิดพลาดจากฝั่ง Client (เช่น ขอของเกินสต็อก, ใส่ quantity เป็น 0)
     * ควรส่งกลับเป็น HTTP 400 Bad Request
     */
    @ExceptionHandler({StockException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleBadRequestException(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}