package org.example.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PaymentMethod implements Comparable<PaymentMethod>  {
    String id;
    int discount;
    BigDecimal limit;
    BigDecimal moneySpent = BigDecimal.ZERO;

    PaymentMethod(){}

    public PaymentMethod(String id, int discount, BigDecimal limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    public String getId() {
        return id;
    }

    public int getDiscount() {
        return discount;
    }
    public BigDecimal getLimit() {
        return limit;
    }

    public BigDecimal getMoneySpent() {
        return moneySpent;
    }

    public void addMoneySpent(BigDecimal money) {
        moneySpent = moneySpent.add(money);
    }

    public BigDecimal getRemainingAmount() {
        return limit.subtract(moneySpent);
    }

    @Override
    public int compareTo(PaymentMethod o) {
        return Integer.compare(o.discount, this.discount);
    }

}