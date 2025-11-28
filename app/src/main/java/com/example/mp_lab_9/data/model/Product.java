package com.example.mp_lab_9.data.model;

public class Product {
    private int id;
    private int listId;
    private String name;
    private int quantity;
    private boolean isPurchased;
    private String createdAt;
    private String category;
    private String notes;
    private double price;
    private String unit;

    // Конструкторы
    public Product(int id, String name, int quantity, boolean isPurchased) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.isPurchased = isPurchased;
    }

    public Product(int id, int listId, String name, int quantity, boolean isPurchased, String createdAt) {
        this.id = id;
        this.listId = listId;
        this.name = name;
        this.quantity = quantity;
        this.isPurchased = isPurchased;
        this.createdAt = createdAt;
    }

    // Полный конструктор для JSON парсинга
    public Product(int id, int listId, String name, int quantity, boolean isPurchased,
                   String createdAt, String category, double price, String notes) {
        this.id = id;
        this.listId = listId;
        this.name = name;
        this.quantity = quantity;
        this.isPurchased = isPurchased;
        this.createdAt = createdAt;
        this.category = category;
        this.price = price;
        this.notes = notes;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isPurchased() { return isPurchased; }
    public void setPurchased(boolean purchased) { isPurchased = purchased; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    // Вспомогательные методы
    public String getFormattedQuantity() {
        if (unit != null && !unit.isEmpty()) {
            return quantity + " " + unit;
        }
        return String.valueOf(quantity);
    }

    public String getFormattedPrice() {
        if (price > 0) {
            return String.format("%.2f ₽", price);
        }
        return "";
    }

    public void togglePurchased() {
        this.isPurchased = !this.isPurchased;
    }
}