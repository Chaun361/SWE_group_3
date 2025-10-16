package com.example.demo.Cart.dto;

import com.example.demo.Cart.model.CartModel;
import java.util.List;


public class CartDTO { 
    // แก้ไข Naming: ใช้ Camel Case (lower case first letter)
    private Long cartId;
    private Long userId;
    // เพิ่ม Fields ที่จำเป็นสำหรับ Response
    private List<CartItemDTO> items; 
    private double totalPrice; 
    
    public CartDTO() {}
    
    // Getters and Setters (ใช้ Camel Case)
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    /**
     * Factory Method สำหรับสร้าง CartDTO ที่มีราคารวมและรายการสินค้า
     */
    public static CartDTO fromEntity(CartModel cart, List<CartItemDTO> itemDTOs, double totalPrice) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUserId());
        dto.setItems(itemDTOs);
        dto.setTotalPrice(totalPrice);
        return dto;
    }
}