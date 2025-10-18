package com.example.demo.Cart.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "Cart")
public class CartModel {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CartId") 
    private Long cartId; 
    
    @Column(name = "UserID", nullable = false)
    private Long userId; 
    
    @Column(name = "IsActive", nullable = false) 
    private boolean isActive = true;

    // ความสัมพันธ์ One-to-Many
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemsModel> cartItems = new ArrayList<>(); 
    
    public CartModel() {
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<CartItemsModel> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemsModel> cartItems) {
        this.cartItems = cartItems;
    }
    
    // เมธอดช่วย (Best Practice)
    public void addItem(CartItemsModel item) {
        this.cartItems.add(item);
        item.setCart(this);
    }
    
    public void removeItem(CartItemsModel item) {
        this.cartItems.remove(item);
        item.setCart(null);
    }
}