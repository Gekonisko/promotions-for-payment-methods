package org.example.model;

import java.math.BigDecimal;
import java.util.List;

public class Order implements Comparable<Order>{
    public String id;
    public BigDecimal value;
    public List<String> promotions;

    public Order(String id, BigDecimal value, List<String> promotions){
        this.id = id;
        this.value = value;
        this.promotions = promotions;
    }

    @Override
    public int compareTo(Order o) {
        return value.compareTo(o.value);
    }
}