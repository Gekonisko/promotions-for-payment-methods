package org.example.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PaymentMethod implements Comparable<PaymentMethod>  {
    public String id;
    public int discount;
    public BigDecimal limit;

    public PaymentMethod(String id, int discount, BigDecimal limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    @Override
    public int compareTo(PaymentMethod o) {
        return Integer.compare(o.discount, this.discount);
    }
}