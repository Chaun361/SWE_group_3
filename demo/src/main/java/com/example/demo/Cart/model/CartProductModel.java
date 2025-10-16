package com.example.demo.Cart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "products") // แมปกับตาราง products
public class CartProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // แก้ไข Naming: Productid -> productId
    @Column(name = "product_id")
    private Long productId; 
    
    // แก้ไข Naming: ProductName -> name
    @Column(name = "product_name")
    private String name; 
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "price")
    private double price;
    
    @Column(name = "stock_quantity")
    private int stockQuantity;

    public CartProductModel() {
    }

    public CartProductModel(Long productId, String name, String description, double price, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // แก้ไข Getter/Setter ให้เป็น Camel Case
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}