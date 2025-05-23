package org.example.service;

import org.example.model.BillResult;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.utils.PercentageUtils;

import java.math.BigDecimal;
import java.util.*;

public class PromotionPaymentCalculator implements PaymentCalculator {

    public PromotionPaymentCalculator() {}

    @Override
    public BillResult calculate(Bill bill) {
        applyFullDiscountPayments(bill);
        applyLoyaltyPayments(bill);
        payRemainingOrders(bill);

        return bill.getBillResult();
    }

    private void applyFullDiscountPayments(Bill bill) {
        List<Order> availableOrders = new ArrayList<>(bill.getUnpaidOrders());

        List<PaymentMethod> sortedMethods = bill.getPaymentMethods().stream()
                .sorted(Comparator.comparingInt(PaymentMethod::getDiscount).reversed())
                .toList();

        for (PaymentMethod paymentMethod : sortedMethods) {
            List<Order> matchingOrders = availableOrders(paymentMethod, availableOrders);

            Optional<Order> bestOrder = matchingOrders.stream()
                    .max(Comparator.comparing(order ->
                            PercentageUtils.calculateDiscountAmount(order.getValue(), paymentMethod.getDiscount()))
                    );

            bestOrder.ifPresent(order -> {
                bill.addPayment(order, paymentMethod, paymentMethod.getLimit());
                availableOrders.remove(order);
            });
        }
    }

    private void applyLoyaltyPayments(Bill bill) {
        var pointsPayments = bill.getPaymentMethod("PUNKTY");
        if(pointsPayments == null) return;

        var points = pointsPayments.getRemainingAmount();

        var unpaidOrders = bill.getUnpaidOrders();
        var ordersForDiscount = availableOrdersForTenPercentDiscount(points, unpaidOrders).stream().sorted().toList();

        for (Order order : ordersForDiscount) {
            var discountAmount = PercentageUtils.calculateDiscountAmount(order.getValue(), 10);
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

            paymentMethods = bill.getRemainingPaymentMethods();
        }
    }

    private List<PaymentMethod> findApplicablePaymentMethods(Bill bill, Order order, List<PaymentMethod> paymentMethods) {
        var amountToPay = order.getValue().subtract(bill.getPaidAmount(order));
        BigDecimal paidAmount = BigDecimal.ZERO;
        List<PaymentMethod> applicablePaymentMethods = new ArrayList<>();

        var sortedPayments = paymentMethods.stream()
                .sorted(Comparator.comparing(PaymentMethod::getRemainingAmount).reversed())
                .toList();

        for (PaymentMethod payment : sortedPayments) {
            paidAmount = paidAmount.add(payment.getRemainingAmount());
            applicablePaymentMethods.add(payment);

            if (paidAmount.compareTo(amountToPay) >= 0) break;
        }

        return applicablePaymentMethods;
    }

    private List<Order> availableOrdersForTenPercentDiscount(BigDecimal points, Collection<Order> orders) {
        return orders.stream()
                .filter(order -> PercentageUtils.calculateDiscountAmount(order.getValue(), 10).compareTo(points) <= 0)
                .toList();
    }

    private List<Order> availableOrders(PaymentMethod paymentMethod, Collection<Order> orders) {
        if(paymentMethod.getId().equals("PUNKTY"))
            return availableOrdersForPoints(paymentMethod, orders);
        return availableOrdersForCreditCards(paymentMethod, orders);
    }

    private List<Order> availableOrdersForCreditCards(PaymentMethod paymentMethod, Collection<Order> orders) {
        return orders.stream()
                .filter((order ->
                        order.getPromotions() != null &&
                        order.getPromotions().contains(paymentMethod.getId()) &&
                        order.getValue().min(paymentMethod.getLimit()).equals(order.getValue())))
                .toList();
    }

    private List<Order> availableOrdersForPoints(PaymentMethod paymentMethod, Collection<Order> orders) {
        return orders.stream()
                .filter((order -> order.getValue().min(paymentMethod.getLimit()).equals(order.getValue())))
                .toList();
    }
}
