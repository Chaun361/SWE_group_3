package com.example.demo.Product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Productid;
    private String ProductName;
    private String Description;
    private double Price;
    private int StockQuantity;

    public ProductModel() {
    }

    public ProductModel(long id, String productName, String description, double price, int stockQuantity) {
        this.Productid = id;
        ProductName = productName;
        Description = description;
        Price = price;
        StockQuantity = stockQuantity;
    }

    public long getId() {
        return Productid;
    }

    public void setId(long id) {
        this.Productid = id;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getStockQuantity() {
        return StockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        StockQuantity = stockQuantity;
    }

}
