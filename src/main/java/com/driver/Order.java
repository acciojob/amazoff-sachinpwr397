package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {
    this.id = id;
    String[] timeParts = deliveryTime.split(":");
    int hours = Integer.parseInt(timeParts[0]);
    int minutes = Integer.parseInt(timeParts[1]);
    this.deliveryTime = hours * 60 + minutes;
}

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
