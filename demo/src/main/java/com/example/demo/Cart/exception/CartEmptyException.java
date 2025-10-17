package com.example.demo.Cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception ที่จะถูกโยนเมื่อพยายามเข้าถึงตะกร้าสินค้าที่ว่างเปล่า
 * การใช้ @ResponseStatus(HttpStatus.NOT_FOUND) จะทำให้ Spring Boot
 * ส่งสถานะ 404 Not Found กลับไปให้ client โดยอัตโนมัติ
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CartEmptyException extends RuntimeException {

    public CartEmptyException(String message) {
        super(message);
    }
}