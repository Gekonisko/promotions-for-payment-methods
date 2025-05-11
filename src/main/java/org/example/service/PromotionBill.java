package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PaymentResult;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class PromotionBill implements Bill {
    List<Order> orders;
    Map<String, List<PaymentResult>> paidOrders;

    List<PaymentMethod> paymentMethods;
    Map<String, BigDecimal> moneySpent;

    PromotionProcessor promotionProcessor;

    public PromotionBill(List<Order> orders, List<PaymentMethod> paymentMethods, PromotionProcessor promotionProcessor) {
        this.orders = orders;
        this.paymentMethods = paymentMethods;

        paidOrders = orders.stream().collect(Collectors.toMap(
                order -> order.id,
                order -> new ArrayList<>()
        ));

        moneySpent = paymentMethods.stream().collect(Collectors.toMap(
                payment -> payment.id,
                payment -> BigDecimal.ZERO
        ));

        this.promotionProcessor = promotionProcessor;
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void addPayment(Order order, PaymentMethod paymentMethod) {
        var amount = getAvailableAmount(order, paymentMethod);

        if(amount.compareTo(BigDecimal.ZERO) == 0) return;

        var amountAfterPromotion = promotionProcessor.process(order, paymentMethod, amount, getPaidAmount(order));

        moneySpent.put(paymentMethod.id, amountAfterPromotion);
        paidOrders.get(order.id).add(new PaymentResult(order, paymentMethod, amountAfterPromotion));
    }

    @Override
    public List<PaymentResult> getBillResult() {
        return paidOrders.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private BigDecimal getPaidAmount(Order order) {
        return paidOrders.get(order.id).stream().map(paymentResult -> paymentResult.paidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getAvailableAmount(Order order, PaymentMethod paymentMethod) {
        var availableMoney = paymentMethod.limit.subtract(moneySpent.get(paymentMethod.id));
        return availableMoney.compareTo(order.value) < 0 ? availableMoney : order.value;
    }
}
