package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PaymentResult;

import java.util.ArrayList;
import java.util.List;

public class PromotionPaymentCalculator implements PaymentCalculator {

    public PromotionPaymentCalculator() {}

    @Override
    public List<PaymentResult> calculate(Bill bill) {
        List<PaymentMethod> paymentsSortedByDiscount = bill.getPaymentMethods().stream().sorted().toList();
        List<PaymentMethod> paymentsByPoints = new ArrayList<>();

        paymentsSortedByDiscount.forEach(payment -> {
            if(payment.id.equals("PUNKTY")) {
                paymentsByPoints.add(payment);
                return;
            }

            var availableOrders = availableOrdersForCreditCards(payment, bill.getOrders());
            var maxOrder = availableOrders.stream().max(Order::compareTo);

            maxOrder.ifPresent(order -> bill.addPayment(order, payment));
        });

        paymentsByPoints.forEach(payment -> {
            var availableOrders = availableOrdersForPoints(payment, bill.getOrders());
            var maxOrder = availableOrders.stream().max(Order::compareTo);

            maxOrder.ifPresent(order -> bill.addPayment(order, payment));
        });

        return bill.getBillResult();
    }

    private List<Order> availableOrdersForCreditCards(PaymentMethod paymentMethod, List<Order> orders) {
        return orders.stream()
                .filter((order -> order.promotions != null && order.promotions.contains(paymentMethod.id) && order.value.min(paymentMethod.limit).equals(order.value)))
                .toList();
    }

    private List<Order> availableOrdersForPoints(PaymentMethod paymentMethod, List<Order> orders) {
        return orders.stream()
                .filter((order -> order.value.min(paymentMethod.limit).equals(order.value)))
                .toList();
    }
}
