package com.example.mp_lab_9.data.model;

public class ShoppingList {
    private int id;
    private int userId;
    private String name;
    private String createdAt;
    private boolean isCompleted;

    public ShoppingList(int id, String name, String createdAt, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
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
}