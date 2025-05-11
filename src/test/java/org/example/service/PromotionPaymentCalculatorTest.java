package org.example.service;

import org.example.model.BillResult;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PaymentResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PromotionPaymentCalculatorTest {

    @Test
    void givenValidOrderAndPromotion_whenCalculate_thenReturnDiscountedPaymentResult() {
        // Given
        Order order = new Order("o1", new BigDecimal(100), List.of("promo-1"));
        PaymentMethod method = new PaymentMethod("promo-1", 10, BigDecimal.valueOf(100));

        Bill bill = new PromotionBill(List.of(order), List.of(method), new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        BillResult results = calculator.calculate(bill);

        // Then
        BillResult expectedResult = new BillResult(List.of(
                new PaymentResult("promo-1", "o1", new BigDecimal("90.00")))
        );
        assertEquals(expectedResult, results);
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
        BillResult results = calculator.calculate(bill);

        // Then
        BillResult expectedResult = new BillResult(List.of(
                new PaymentResult("BosBankrut", "ORDER2", new BigDecimal("190.00")),
                new PaymentResult("mZysk", "ORDER3", new BigDecimal("135.00")),
                new PaymentResult("PUNKTY", "ORDER1", new BigDecimal("85.00")),
                new PaymentResult("PUNKTY", "ORDER4", new BigDecimal("15.00")),
                new PaymentResult("mZysk", "ORDER4", new BigDecimal("30.00"))
        ));

        assertEquals(expectedResult, results);
    }

    @Test
    void test1_shouldApplyCardDiscountWhenFullyPaidWithEligibleCard() {
        // Given
        List<Order> orders = List.of(
                new Order("ORDER1", new BigDecimal("100.00"), List.of("mZysk"))
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("mZysk", 10, new BigDecimal("200.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        BillResult results = calculator.calculate(bill);

        // Then
        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("mZysk", "ORDER1", new BigDecimal("90.00"))
        ));

        assertEquals(expectedResults, results);
    }


    @Test
    void test2_shouldApplyPointsDiscountWhenAtLeast10PercentPaidWithPoints() {
        // Given
        List<Order> orders = List.of(
                new Order("ORDER2", new BigDecimal("100.00"), List.of())
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("20.00")),
                new PaymentMethod("mZysk", 10, new BigDecimal("200.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        BillResult results = calculator.calculate(bill);

        // Then
        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("PUNKTY", "ORDER2", new BigDecimal("20.00")),
                new PaymentResult("mZysk", "ORDER2", new BigDecimal("70.00"))
        ));

        assertEquals(expectedResults, results);
    }

    @Test
    void test3_shouldPreferPointsIfSameDiscountAsCard() {
        // Given
        List<Order> orders = List.of(
                new Order("ORDER3", new BigDecimal("100.00"), List.of("mZysk"))
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 10, new BigDecimal("100.00")),
                new PaymentMethod("mZysk", 10, new BigDecimal("100.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        BillResult results = calculator.calculate(bill);

        // Then
        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("PUNKTY", "ORDER3", new BigDecimal("90.00"))
        ));

        assertEquals(expectedResults, results);
    }

    @Test
    void test4_shouldApplyPointsOnlyDiscountWhenFullyPaidWithPoints() {
        // Given
        List<Order> orders = List.of(
                new Order("ORDER5", new BigDecimal("100.00"), List.of())
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("100.00")),
                new PaymentMethod("BosBankrut", 5, new BigDecimal("50.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        BillResult results = calculator.calculate(bill);

        // Then
        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("PUNKTY", "ORDER5", new BigDecimal("85.00"))
        ));

        assertEquals(expectedResults, results);
    }

    @Test
    void test5_shouldNotApplyDiscountWhenPointsBelowTenPercent() {
        List<Order> orders = List.of(
                new Order("ORDER7", new BigDecimal("100.00"), List.of())
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("9.99")),
                new PaymentMethod("mZysk", 10, new BigDecimal("200.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        BillResult results = calculator.calculate(bill);

        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("PUNKTY", "ORDER7", new BigDecimal("9.99")),
                new PaymentResult("mZysk", "ORDER7", new BigDecimal("90.01"))
        ));

        assertEquals(expectedResults, results);
    }

    @Test
    void test6_shouldNotApplyAnyDiscountWhenNoPromotionsAvailable() {
        List<Order> orders = List.of(
                new Order("ORDER8", new BigDecimal("100.00"), List.of())
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("BosBankrut", 5, new BigDecimal("100.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        BillResult results = calculator.calculate(bill);

        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("BosBankrut", "ORDER8", new BigDecimal("100.00"))
        ));

        assertEquals(expectedResults, results);
    }

    @Test
    void test7_shouldChooseCardWhenPointsAreBetterButInsufficient() {
        List<Order> orders = List.of(
                new Order("ORDER9", new BigDecimal("100.00"), List.of("mZysk"))
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("80.00")),
                new PaymentMethod("mZysk", 10, new BigDecimal("100.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        BillResult results = calculator.calculate(bill);

        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("mZysk", "ORDER9", new BigDecimal("90.00"))
        ));

        assertEquals(expectedResults, results);
    }


    @Test
    void test8_shouldThrowExceptionWhenNoAvailableFunds() {
        List<Order> orders = List.of(
                new Order("ORDER10", new BigDecimal("100.00"), List.of("mZysk"))
        );
        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("5.00")),
                new PaymentMethod("mZysk", 10, new BigDecimal("0.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        assertThrows(Exception.class, () -> calculator.calculate(bill));
    }

    @Test
    void test9_shouldDistributePaymentsOptimallyAcrossMultipleOrders() {
        // Given
        List<Order> orders = List.of(
                new Order("ORDER1", new BigDecimal("100.00"), List.of("mZysk")),
                new Order("ORDER2", new BigDecimal("150.00"), List.of("BosBankrut")),
                new Order("ORDER3", new BigDecimal("200.00"), List.of("mZysk", "BosBankrut")),
                new Order("ORDER4", new BigDecimal("80.00"), List.of()),
                new Order("ORDER5", new BigDecimal("50.00"), List.of())
        );

        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("120.00")),
                new PaymentMethod("mZysk", 10, new BigDecimal("200.00")),
                new PaymentMethod("BosBankrut", 5, new BigDecimal("200.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        BillResult results = calculator.calculate(bill);

        // Then
        BillResult expectedResults = new BillResult(List.of(
                // ORDER3: mZysk daje większy rabat niż BosBankrut (200 * 10% = 20 > 10)
                new PaymentResult("mZysk", "ORDER3", new BigDecimal("180.00")),

                // ORDER2: zostaje BosBankrut: 150 - 5% = 142.50
                new PaymentResult("BosBankrut", "ORDER2", new BigDecimal("142.50")),

                // ORDER1: możemy w całości zapłacić punktami (rabat 15%)
                new PaymentResult("PUNKTY", "ORDER1", new BigDecimal("85.00")),

                // ORDER4: 10% z 80 = 8 punktów → rabat 10% = 8 zł → płacimy 72 zł
                new PaymentResult("PUNKTY", "ORDER4", new BigDecimal("8.00")),
                new PaymentResult("BosBankrut", "ORDER4", new BigDecimal("64.00")),

                // ORDER5: 10% z 50 = 5 punktów → rabat 10% = 5 zł → płacimy 45 zł
                new PaymentResult("PUNKTY", "ORDER5", new BigDecimal("5.00")),
                new PaymentResult("mZysk", "ORDER5", new BigDecimal("40.00"))
        ));

        assertEquals(expectedResults, results);
    }


    @Test
    void test10_shouldUseCardsOnlyWhenTheyGiveBetterDiscountThanPoints() {
        List<Order> orders = List.of(
                new Order("ORDER1", new BigDecimal("100.00"), List.of("BankA")),
                new Order("ORDER2", new BigDecimal("200.00"), List.of("BankB")),
                new Order("ORDER3", new BigDecimal("300.00"), List.of("BankC"))
        );

        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 10, new BigDecimal("500.00")),  // 10% = mniej niż karty
                new PaymentMethod("BankA", 15, new BigDecimal("100.00")),
                new PaymentMethod("BankB", 20, new BigDecimal("200.00")),
                new PaymentMethod("BankC", 25, new BigDecimal("300.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();
        BillResult results = calculator.calculate(bill);

        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("BankA", "ORDER1", new BigDecimal("85.00")),   // 15% rabatu
                new PaymentResult("BankB", "ORDER2", new BigDecimal("160.00")),  // 20%
                new PaymentResult("BankC", "ORDER3", new BigDecimal("225.00"))   // 25%
        ));

        assertEquals(expectedResults, results);
    }



    @Test
    void test11_shouldUsePointsWhereTheyGiveMaxSavingsAcrossManyOrders() {
        // Given
        List<Order> orders = List.of(
                new Order("ORDER1", new BigDecimal("100.00"), List.of()),
                new Order("ORDER2", new BigDecimal("300.00"), List.of()),
                new Order("ORDER3", new BigDecimal("150.00"), List.of())
        );

        List<PaymentMethod> paymentMethods = List.of(
                new PaymentMethod("PUNKTY", 20, new BigDecimal("120.00")),
                new PaymentMethod("BankA", 5, new BigDecimal("500.00"))
        );

        Bill bill = new PromotionBill(orders, paymentMethods, new PromotionProcessorImpl());
        PaymentCalculator calculator = new PromotionPaymentCalculator();

        // When
        BillResult results = calculator.calculate(bill);

        // Then
        BillResult expectedResults = new BillResult(List.of(
                new PaymentResult("PUNKTY", "ORDER1", new BigDecimal("80.00")),
                new PaymentResult("PUNKTY", "ORDER2", new BigDecimal("30.00")),
                new PaymentResult("BankA", "ORDER2", new BigDecimal("240.00")),
                new PaymentResult("BankA", "ORDER3", new BigDecimal("150.00"))
        ));

        Comparator<PaymentResult> comparator = Comparator
                .comparing(PaymentResult::getPaymentId)
                .thenComparing(PaymentResult::getOrderId);

        assertEquals(expectedResults, results);
    }
}
