package org.example.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class PaymentResult {
     String paymentId;
     String orderId;
     BigDecimal paidAmount;
     BigDecimal discountAmount;

    public PaymentResult(Order order, PaymentMethod paymentMethod, BigDecimal paidAmount, BigDecimal discountAmount) {
        paymentId = paymentMethod.id;
        orderId = order.id;
        this.paidAmount = paidAmount;
        this.discountAmount = discountAmount;
    }

    public PaymentResult(String paymentId, String orderId, BigDecimal paidAmount) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paidAmount = paidAmount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public BigDecimal getFullPaidAmount() {
        return paidAmount.add(discountAmount);
    }

    @Override
    public String toString() {
        return  paymentId + "\t" + orderId + "\t" + paidAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentResult that = (PaymentResult) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(paymentId, that.paymentId) &&
                paidAmount.compareTo(that.paidAmount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, paymentId, paidAmount.setScale(2, RoundingMode.HALF_UP));
    }
}
