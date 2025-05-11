package org.example.service;

import org.example.model.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PromotionBill implements Bill {
    Map<String, Order> orders;
    Map<String, List<PaymentResult>> paidOrders;

    Map<String, PaymentMethod> paymentMethods;

    PromotionProcessor promotionProcessor;

    public PromotionBill(List<Order> orders, List<PaymentMethod> paymentMethods, PromotionProcessor promotionProcessor) {
        this.orders = orders.stream().collect(Collectors.toMap(
                order -> order.id,
                order -> order
        ));
        this.paymentMethods = paymentMethods.stream().collect(Collectors.toMap(
                PaymentMethod::getId,
                payment -> payment
        ));


        paidOrders = orders.stream().collect(Collectors.toMap(
                order -> order.id,
                order -> new ArrayList<>()
        ));

        this.promotionProcessor = promotionProcessor;
    }

    @Override
    public Collection<Order> getOrders() {
        return orders.values();
    }

    @Override
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    @Override
    public Collection<PaymentMethod> getPaymentMethods() {
        return paymentMethods.values();
    }

    @Override
    public PaymentMethod getPaymentMethod(String paymentId) {
        return paymentMethods.get(paymentId);
    }

    @Override
    public void addPayment(Order order, PaymentMethod paymentMethod, BigDecimal paymentAmount) {
        var amount = calculateApplicableAmount(order, paymentMethod, paymentAmount);

        if(amount.compareTo(BigDecimal.ZERO) == 0) return;

        PromotionResult promotionResult = promotionProcessor.process(order, paymentMethod, amount, getPaidAmount(order));

        paymentMethods.get(paymentMethod.getId()).addMoneySpent(promotionResult.getFinalAmount());
        paidOrders.get(order.id).add(new PaymentResult(order, paymentMethod, promotionResult.getFinalAmount(), promotionResult.getDiscountAmount()));
    }

    @Override
    public BillResult getBillResult() {
        var payments = paidOrders.values().stream().flatMap(List::stream).collect(Collectors.toList());
        return new BillResult(payments);
    }

    @Override
    public List<Order> getUnpaidOrders() {
        var unpaidOrders = new ArrayList<Order>();
        for (Map.Entry<String, List<PaymentResult>> entry : paidOrders.entrySet()) {
            var sum = entry.getValue().stream().map(PaymentResult::getFullPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            var order = orders.get(entry.getKey());

            if(sum.compareTo(order.value) < 0)
                unpaidOrders.add(order);
        }
        return unpaidOrders;
    }

    @Override
    public List<PaymentMethod> getRemainingPaymentMethods() {
        return getPaymentMethods().stream()
                .filter(paymentMethod -> paymentMethod.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getPaidAmount(Order order) {
        return paidOrders.get(order.id).stream().map(PaymentResult::getFullPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateApplicableAmount(Order order, PaymentMethod paymentMethod, BigDecimal paymentAmount) {
        var availableMoney = paymentMethod.getLimit().subtract(paymentMethod.getMoneySpent());
        var amountToPay = order.value.subtract(getPaidAmount(order));

        return Stream.of(paymentAmount, availableMoney, amountToPay)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }
}
