package com.example.clover_shoes.model;

public class ProductSize {
    private int size;
    private String position;
    private int quantity;

    public ProductSize() {
    }

    public ProductSize(int size, String position, int quantity) {
        this.size = size;
        this.position = position;
        this.quantity = quantity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
