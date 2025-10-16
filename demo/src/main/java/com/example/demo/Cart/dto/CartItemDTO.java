package com.example.demo.Cart.dto;

import com.example.demo.Cart.model.CartItemsModel;
import com.example.demo.Product.model.ProductModel;

public class CartItemDTO {
    
    private Long productId;
    private String productName;
    private int quantity;
    private double itemPrice; 
    private double itemTotalPrice; 

    public CartItemDTO() {}
    
    // Getters and Setters (ใช้ Camel Case)
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getItemPrice() { return itemPrice; }
    public void setItemPrice(double itemPrice) { this.itemPrice = itemPrice; }
    public double getItemTotalPrice() { return itemTotalPrice; }
    public void setItemTotalPrice(double itemTotalPrice) { this.itemTotalPrice = itemTotalPrice; }
    
    /**
     * แปลง CartItem และ Product Model เป็น DTO
     */
    public static CartItemDTO fromEntity(CartItemsModel item, ProductModel product) {
        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(product.getProductName()); 
        dto.setQuantity(item.getQuantity());
        dto.setItemPrice(product.getPrice());
        dto.setItemTotalPrice(item.getQuantity() * product.getPrice());
        return dto;
    }
}