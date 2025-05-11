package org.example.service;

import org.example.model.BillResult;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.utils.PercentageUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class PromotionPaymentCalculator implements PaymentCalculator {

    public PromotionPaymentCalculator() {}

    @Override
    public BillResult calculate(Bill  bill) {
        applyFullDiscountPayments(bill);
        applyLoyaltyPayments(bill);
        //payRemainingOrders(bill);

        return bill.getBillResult();
    }

    private void applyFullDiscountPayments(Bill bill) {
        List<PaymentMethod> paymentsSortedByDiscount = bill.getPaymentMethods().stream().sorted().toList();
        var unpaidOrders = bill.getUnpaidOrders();

        for (PaymentMethod payment : paymentsSortedByDiscount) {
            var availableOrders = availableOrders(payment, unpaidOrders);
            var maxOrder = availableOrders.stream().max(Order::compareTo);
            maxOrder.ifPresent(order -> bill.addPayment(order, payment, payment.getLimit()));
        }
    }

    private void applyLoyaltyPayments(Bill bill) {
        var pointsPayments = bill.getPaymentMethod("PUNKTY");
        var points = pointsPayments.getRemainingAmount();

        var unpaidOrders = bill.getUnpaidOrders();
        var ordersForDiscount = availableOrdersForTenPercentDiscount(points, unpaidOrders).stream().sorted().toList();

        for (Order order : ordersForDiscount) {
            var discountAmount = PercentageUtils.calculateDiscountAmount(order.value, 10);
            if(points.compareTo(discountAmount) >= 0)
            {
                bill.addPayment(order, pointsPayments, points);
            }
            else break;
            points = bill.getPaymentMethod(pointsPayments.getId()).getRemainingAmount();
        }

        applyRemainingLoyaltyPoints(bill);
    }

    private void applyRemainingLoyaltyPoints(Bill bill) {
        var unpaidOrders = bill.getUnpaidOrders().stream().sorted().toList();
        var pointsPayments = bill.getPaymentMethod("PUNKTY");
        var points = pointsPayments.getRemainingAmount();

        for (Order order : unpaidOrders) {
            if(points.compareTo(BigDecimal.ZERO) == 0) break;

            bill.addPayment(order, pointsPayments, points);
            points = bill.getPaymentMethod(pointsPayments.getId()).getRemainingAmount();
        }
    }

    private void payRemainingOrders(Bill bill) {
        var unpaidOrders = bill.getUnpaidOrders();
        List<PaymentMethod> paymentMethods = bill.getRemainingPaymentMethods();

        for (Order order : unpaidOrders) {
            var payments = findApplicablePaymentMethods(bill, order, paymentMethods);
            if(payments.isEmpty()) break;

            payments.forEach(payment -> bill.addPayment(order, payment, payment.getLimit()));

            paymentMethods = bill.getRemainingPaymentMethods().stream()
                    .filter(payment -> "PUNKTY".equalsIgnoreCase(payment.getId()))
                    .toList();
        }
    }

    private List<PaymentMethod> findApplicablePaymentMethods(Bill bill, Order order, List<PaymentMethod> paymentMethods) {



        return List.of();
    }

    private List<Order> availableOrdersForTenPercentDiscount(BigDecimal points, Collection<Order> orders) {
        return orders.stream()
                .filter(order -> PercentageUtils.calculateDiscountAmount(order.value, 10).compareTo(points) <= 0)
                .toList();
    }

    private List<Order> availableOrders(PaymentMethod paymentMethod, Collection<Order> orders) {
        if(paymentMethod.getId().equals("PUNKTY"))
            return availableOrdersForPoints(paymentMethod, orders);
        return availableOrdersForCreditCards(paymentMethod, orders);
    }

    private List<Order> availableOrdersForCreditCards(PaymentMethod paymentMethod, Collection<Order> orders) {
        return orders.stream()
                .filter((order -> order.promotions != null && order.promotions.contains(paymentMethod.getId()) && order.value.min(paymentMethod.getLimit()).equals(order.value)))
                .toList();
    }

    private List<Order> availableOrdersForPoints(PaymentMethod paymentMethod, Collection<Order> orders) {
        return orders.stream()
                .filter((order -> order.value.min(paymentMethod.getLimit()).equals(order.value)))
                .toList();
    }
}
