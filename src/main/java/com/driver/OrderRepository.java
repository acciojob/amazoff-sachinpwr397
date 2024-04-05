package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<>();
        this.partnerMap = new HashMap<>();
        this.partnerToOrderMap = new HashMap<>();
        this.orderToPartnerMap = new HashMap<>();
    }

    public void saveOrder(Order order){
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, partner);
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
            orders.add(orderId);
            partnerToOrderMap.put(partnerId, orders);
            partnerMap.get(partnerId).setNumberOfOrders(partnerMap.get(partnerId).getNumberOfOrders() + 1);
            orderToPartnerMap.put(orderId, partnerId);
        }
    }

    public Order findOrderById(String orderId){
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        return orders.size();
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        return new ArrayList<>(orders);
    }

    public List<String> findAllOrders(){
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId){
        partnerMap.remove(partnerId);
        // Reassign orders from deleted partner to unassigned
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        for (String orderId : orders) {
            orderToPartnerMap.remove(orderId);
        }
        partnerToOrderMap.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        String partnerId = orderToPartnerMap.get(orderId);
        if (partnerId != null) {
            partnerToOrderMap.get(partnerId).remove(orderId);
            partnerMap.get(partnerId).setNumberOfOrders(partnerMap.get(partnerId).getNumberOfOrders() - 1);
            orderToPartnerMap.remove(orderId);
        }
        orderMap.remove(orderId);
    }

    public Integer findCountOfUnassignedOrders(){
        int unassignedCount = 0;
        for (String orderId : orderMap.keySet()) {
            if (!orderToPartnerMap.containsKey(orderId)) {
                unassignedCount++;
            }
        }
        return unassignedCount;
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        int time = convertTimeToMinutes(timeString);
        int count = 0;
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        for (String orderId : orders) {
            Order order = orderMap.get(orderId);
            if (order != null && order.getDeliveryTime() > time) {
                count++;
            }
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        int lastDeliveryTime = Integer.MIN_VALUE;
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        for (String orderId : orders) {
            Order order = orderMap.get(orderId);
            if (order != null && order.getDeliveryTime() > lastDeliveryTime) {
                lastDeliveryTime = order.getDeliveryTime();
            }
        }
        return convertMinutesToTime(lastDeliveryTime);
    }

    private int convertTimeToMinutes(String timeString) {
        String[] parts = timeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private String convertMinutesToTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}
