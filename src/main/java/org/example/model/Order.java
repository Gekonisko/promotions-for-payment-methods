package org.example.model;

import java.math.BigDecimal;
import java.util.List;

public class Order implements Comparable<Order>{
    String id;
    BigDecimal value;
    List<String> promotions;

    Order(){}

    public Order(String id, BigDecimal value, List<String> promotions){
        this.id = id;
        this.value = value;
        this.promotions = promotions;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public List<String> getPromotions() {
        return promotions;
    }

    @Override
    public int compareTo(Order o) {
        return value.compareTo(o.value);
    }
}