package com.example.demo.Order.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders") // กำหนดชื่อตารางในฐานข้อมูล
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID") // แมปกับคอลัมน์ชื่อ OrderID
    private long OrderID;

    @Column(name = "UserID", nullable = false) // แมปกับคอลัมน์ชื่อ UserID
    private long UserID;

    @Column(name = "OrderDate", nullable = false) // แมปกับคอลัมน์ชื่อ OrderDate
    private LocalDateTime OrderDate; // <-- แก้เป็น LocalDateTime

    @Column(name = "TotalAmount") // แมปกับคอลัมน์ชื่อ TotalAmount
    private double TotalAmount; // <-- แก้เป็น double

    @Column(name = "Status") // แมปกับคอลัมน์ชื่อ Status
    private String Status;
    
    // ความสัมพันธ์กับ OrderItemsModel: หนึ่ง Order มีได้หลาย Item
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemModel> orderItems;


    // Constructor ที่จำเป็นสำหรับ JPA
    public OrderModel() {
    }
    
    // --- Getters and Setters ---
    // ชื่อเมธอดจะยังคงเดิมตามที่คุณต้องการ

    public long getOrderID() {
        return OrderID;
    }

    public void setOrderID(long orderID) {
        OrderID = orderID;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }

    public LocalDateTime getOrderDate() { // <-- แก้ Type ที่ return
        return OrderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) { // <-- แก้ Type ที่รับเข้ามา
        OrderDate = orderDate;
    }

    public double getTotalAmount() { // <-- แก้ Type ที่ return
        return TotalAmount;
    }

    public void setTotalAmount(double totalAmount) { // <-- แก้ Type ที่รับเข้ามา
        TotalAmount = totalAmount;
    }

    public String getStatus() { // <-- แก้ Type ที่ return ให้ตรงกับตัวแปร
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<OrderItemModel> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemModel> orderItems) {
        this.orderItems = orderItems;
    }
}