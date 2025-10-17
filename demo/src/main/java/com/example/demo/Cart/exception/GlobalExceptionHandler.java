package com.example.demo.Cart.exception; // หรือ package หลักของคุณ

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * จัดการเมื่อตะกร้าว่างเปล่า (CartEmptyException)
     * โดยจะส่งกลับเป็น HTTP 204 No Content ซึ่งจะไม่มี body
     */
    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<Object> handleCartEmptyException(CartEmptyException ex, WebRequest request) {
        return ResponseEntity.noContent().build();
    }

    /**
     * จัดการ ResourceNotFoundException (เช่น หา cart, user, product ไม่เจอ)
     * ควรส่งกลับเป็น HTTP 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());

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