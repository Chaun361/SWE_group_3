package com.example.demo.Order.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class OrderHistoryDTO {

    private Long orderId;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderHistoryItemDTO> orderItems;
    private double total;

    // 🧱 Constructor ว่าง (จำเป็นสำหรับ JSON serialization/deserialization)
    public OrderHistoryDTO() {}

    // 🧱 Constructor สำหรับสร้าง object อย่างรวดเร็ว
    public OrderHistoryDTO(Long orderId, LocalDateTime orderDate, String status, 
                           List<OrderHistoryItemDTO> orderItems, double total) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.orderItems = orderItems;
        this.total = total;
    }

    // 📦 Getter และ Setter
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderHistoryItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderHistoryItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // 🧾 Optional: เพื่อ debug ได้ง่าย
    @Override
    public String toString() {
        return "OrderHistoryDTO{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", orderItems=" + orderItems +
                ", total=" + total +
                '}';
    }
}
