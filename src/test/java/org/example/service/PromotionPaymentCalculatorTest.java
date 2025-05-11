package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PaymentResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PromotionPaymentCalculatorTest {

    @Test
    void givenValidOrderAndPromotion_whenCalculate_thenReturnDiscountedPaymentResult() {
        // Given
        Order order = new Order("o1", new BigDecimal(100), List.of("promo-1"));
        PaymentMethod method = new PaymentMethod("promo-1", 10, BigDecimal.valueOf(100));

        Bill bill = new PromotionBill(List.of(order), List.of(method), new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        List<PaymentResult> results = calculator.calculate(bill);

        // Then
        assertEquals(1, results.size());
        assertEquals(new BigDecimal("90.00"), results.get(0).paidAmount);
    }

    @Test
    void givenOcadoExample_whenCalculate_thenReturnDiscountedPaymentResult() {
        // Given
        List<Order> orders = List.of(
                new Order("ORDER1", new BigDecimal("100.00"), List.of("mZysk")),
                new Order("ORDER2", new BigDecimal("200.00"), List.of("BosBankrut")),
                new Order("ORDER3", new BigDecimal("150.00"), List.of("mZysk", "BosBankrut")),
                new Order("ORDER4", new BigDecimal("50.00"), List.of()) // brak promocji
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("100.00")),
                new PaymentMethod("mZysk", 10, new BigDecimal("180.00")),
                new PaymentMethod("BosBankrut", 5, new BigDecimal("200.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        List<PaymentResult> results = calculator.calculate(bill);

        // Then
        var comparator = Comparator.comparing(PaymentResult::getPaymentId)
                .thenComparing(PaymentResult::getOrderId);

        List<PaymentResult> expectedResults = List.of(
                new PaymentResult("BosBankrut", "ORDER2", new BigDecimal("190.00")),
                new PaymentResult("mZysk", "ORDER3", new BigDecimal("135.00")),
                new PaymentResult("PUNKTY", "ORDER1", new BigDecimal("85.00")),
                new PaymentResult("PUNKTY", "ORDER4", new BigDecimal("15.00")),
                new PaymentResult("mZysk", "ORDER4", new BigDecimal("30.00"))
        );

        assertEquals(expectedResults.size(), results.size());
        assertEquals(expectedResults.stream()
                .sorted(comparator)
                .toList(),

                results.stream()
                        .sorted(comparator)
                        .toList()
        );
    }
}
