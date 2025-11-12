package com.example.demo.Order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "OrderItems") // กำหนดชื่อตาราง
public class OrderItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderItemID")
    private long OrderItemID;

    // ความสัมพันธ์กับ OrderModel: หลาย Item อยู่ใน 1 Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderID", nullable = false) // Foreign Key
    @JsonIgnore // ป้องกันการวนลูปข้อมูลตอนแปลงเป็น JSON
    private OrderModel order;

    @Column(name = "ProductID", nullable = false)
    private long ProductID;

    @Column(name = "Quantity")
    private int Quantity; // <-- แก้เป็น int

    @Column(name = "UnitPrice") // <-- เปลี่ยนชื่อคอลัมน์เพื่อความชัดเจน
    private double PricePerUnit; // <-- แก้เป็น double

    // Constructor ที่จำเป็นสำหรับ JPA
    public OrderItemModel() {
    }

    // --- Getters and Setters ---

    public long getOrderItemID() {
        return OrderItemID;
    }

    public void setOrderItemID(long orderItemID) {
        OrderItemID = orderItemID;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public long getProductID() {
        return ProductID;
    }

    public void setProductID(long productID) {
        ProductID = productID;
    }

    public int getQuantity() { // <-- แก้ Type
        return Quantity;
    }

    public void setQuantity(int quantity) { // <-- แก้ Type
        Quantity = quantity;
    }

    public double getUnitPrice() { // <-- แก้ Type และชื่อเมธอด
        return PricePerUnit;
    }

    public void setUnitPrice(double pricePerUnit) { // <-- แก้ Type และชื่อเมธอด
        PricePerUnit = pricePerUnit;
    }
}