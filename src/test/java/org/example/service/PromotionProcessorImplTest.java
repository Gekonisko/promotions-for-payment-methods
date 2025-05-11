package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PromotionResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PromotionProcessorImplTest {
    PromotionProcessor processor = new PromotionProcessorImpl();

    @Test
    void givenFullPaymentWithBankCard_whenProcess_thenApplyMethodDiscount() {
        Order order = new Order("ORDER1", new BigDecimal("100"), List.of("CARD"));
        PaymentMethod method = new PaymentMethod("CARD", 20, new BigDecimal("100"));

        PromotionResult result = processor.process(order, method, new BigDecimal("100"), BigDecimal.ZERO);

        assertEquals(new BigDecimal("80.00"), result.getFinalAmount());
    }

    @Test
    void givenPartialPointsPaymentOver10Percent_whenProcess_thenApply10PercentLoyaltyDiscount() {
        Order order = new Order("ORDER1", new BigDecimal("100"), List.of("PUNKTY"));
        PaymentMethod method = new PaymentMethod("PUNKTY", 15, new BigDecimal("100"));

        PromotionResult result = processor.process(order, method, new BigDecimal("20"), BigDecimal.ZERO);

        assertEquals(new BigDecimal("20"), result.getFinalAmount()); // 15% rabat za punkty
    }

    @Test
    void givenFullPointsPayment_whenProcess_thenApplyPUNKTYMethodDiscount() {
        Order order = new Order("ORDER1", new BigDecimal("100"), List.of("PUNKTY"));
        PaymentMethod method = new PaymentMethod("PUNKTY", 15, new BigDecimal("100"));

        PromotionResult result = processor.process(order, method, new BigDecimal("100"), BigDecimal.ZERO);

        assertEquals(new BigDecimal("85.00"), result.getFinalAmount()); // rabat PUNKTY zamiast 10%
    }

    @Test
    void givenPartialPaymentBelow10PercentWithPoints_whenProcess_thenDoNotApplyAnyDiscount() {
        Order order = new Order("ORDER1", new BigDecimal("100"), List.of("PUNKTY"));
        PaymentMethod method = new PaymentMethod("PUNKTY", 15, new BigDecimal("100"));

        PromotionResult result = processor.process(order, method, new BigDecimal("5.00"), BigDecimal.ZERO);

        assertEquals(new BigDecimal("5.00"), result.getFinalAmount()); // brak rabatu
    }

    @Test
    void givenNonMatchingMethod_whenProcess_thenDoNotApplyAnyDiscount() {
        Order order = new Order("ORDER1", new BigDecimal("100"), List.of("INNY_BANK"));
        PaymentMethod method = new PaymentMethod("CARD", 20, new BigDecimal("100"));

        PromotionResult result = processor.process(order, method, new BigDecimal("100"), BigDecimal.ZERO);

        assertEquals(new BigDecimal("100"), result.getFinalAmount()); // brak zgodności – brak rabatu
    }
}
