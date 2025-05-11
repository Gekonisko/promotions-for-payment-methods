package org.example.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BillResult {

    List<PaymentResult> payments;
    Map<String, BigDecimal> amountPerPayment = new HashMap<>();

    public BillResult(List<PaymentResult> payments) {
        this.payments = payments;

        amountPerPayment = payments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.paymentId,
                        Collectors.reducing(BigDecimal.ZERO, payment -> payment.paidAmount, BigDecimal::add)
                ));
    }

    public List<PaymentResult> getPayments() {
        return payments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillResult that)) return false;
        return Objects.equals(amountPerPayment, that.amountPerPayment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountPerPayment);
    }

    @Override
    public String toString() {
        return amountPerPayment.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }
}
