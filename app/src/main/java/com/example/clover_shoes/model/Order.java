package com.example.clover_shoes.model;

import java.util.ArrayList;
import java.util.List;

public class Order {


    private int id;
    private String time;
    private int totalPrice;
    private ArrayList<OrderDetail> list;

    public Order(int id, String time, int totalPrice, ArrayList<OrderDetail> list) {
        this.id = id;
        this.time = time;
        this.totalPrice = totalPrice;
        this.list = list;
    }

    public Order(int id, String time, int totalPrice) {
        this.id = id;
        this.time = time;
        this.totalPrice = totalPrice;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<OrderDetail> getList() {
        return list;
    }

    public void setList(ArrayList<OrderDetail> list) {
        this.list = list;
    }


}
