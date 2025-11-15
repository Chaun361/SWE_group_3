package com.example.demo.Order.DTO;

public class OrderHistoryItemDTO {

    private Long productId;
    private String productName;
    private double pricePerUnit;
    private int quantity;

    // 🧱 Constructor ว่าง (จำเป็นสำหรับการแปลง JSON)
    public OrderHistoryItemDTO() {}

    // 🧱 Constructor สำหรับสร้าง object อย่างรวดเร็ว
    public OrderHistoryItemDTO(Long productId, String productName, double pricePerUnit, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
    }

    // 📦 Getter และ Setter
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // 🧾 Optional: เพิ่ม toString() เพื่อ debug ได้ง่าย
    @Override
    public String toString() {
        return "OrderHistoryItemDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                ", quantity=" + quantity +
                '}';
    }
}