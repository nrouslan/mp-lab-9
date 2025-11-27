package com.example.mp_lab_9.data.model;

import java.util.Date;

public class ShoppingList {
    private int id;
    private int userId;
    private String name;
    private String createdAt;
    private boolean isCompleted;
    private int totalProducts;
    private int purchasedProducts;
    private Date lastModified;

    public ShoppingList(int id, String name, String createdAt, int totalProducts, int purchasedProducts, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.totalProducts = totalProducts;
        this.purchasedProducts = purchasedProducts;
        this.isCompleted = isCompleted;
    }

    public ShoppingList(int id, int userId, String name, String createdAt, boolean isCompleted) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt;
        this.isCompleted = isCompleted;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }

    public int getPurchasedProducts() { return purchasedProducts; }
    public void setPurchasedProducts(int purchasedProducts) { this.purchasedProducts = purchasedProducts; }

    public Date getLastModified() { return lastModified; }
    public void setLastModified(Date lastModified) { this.lastModified = lastModified; }

    // Вспомогательные методы
    public int getProgressPercentage() {
        if (totalProducts == 0) return 0;
        return (int) ((purchasedProducts * 100.0f) / totalProducts);
    }

    public boolean isEmpty() {
        return totalProducts == 0;
    }
}