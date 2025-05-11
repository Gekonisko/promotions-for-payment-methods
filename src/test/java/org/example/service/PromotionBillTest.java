package org.example.service;

import org.example.model.BillResult;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PromotionBillTest {

    private Order order1;
    private Order order2;
    private PaymentMethod method1;
    private PaymentMethod method2;
    private Bill bill;

    @BeforeEach
    void setUp() {
        order1 = new Order("ORDER1", new BigDecimal("100.00"), List.of("CARD"));
        order2 = new Order("ORDER2", new BigDecimal("200.00"), List.of("POINTS"));

        method1 = new PaymentMethod("CARD", 10, new BigDecimal("150.00"));
        method2 = new PaymentMethod("POINTS", 15, new BigDecimal("50.00"));

        List<Order> orders = List.of(order1, order2);
        List<PaymentMethod> methods = List.of(method1, method2);

        PromotionProcessor processor = new PromotionProcessorImpl();
        bill = new PromotionBill(orders, methods, processor);
    }

    @Test
    void shouldReturnAllOrders() {
        assertEquals(2, bill.getOrders().size());
    }

    @Test
    void shouldReturnOrderById() {
        Order fetched = bill.getOrder("ORDER1");
        assertEquals(order1, fetched);
    }

    @Test
    void shouldReturnPaymentMethodById() {
        PaymentMethod fetched = bill.getPaymentMethod("CARD");
        assertEquals(method1, fetched);
    }

    @Test
    void shouldAddPaymentAndUpdatePaidAmount() {
        bill.addPayment(order1, method1, new BigDecimal("80.00"));
        assertEquals(new BigDecimal("80.00"), bill.getPaidAmount(order1));
    }

    @Test
    void shouldReturnUnpaidOrdersInitially() {
        List<Order> unpaid = bill.getUnpaidOrders();
        assertTrue(unpaid.contains(order1));
        assertTrue(unpaid.contains(order2));
    }

    @Test
    void shouldRemoveOrderFromUnpaidAfterFullPayment() {
        bill.addPayment(order1, method1, new BigDecimal("100.00"));
        List<Order> unpaid = bill.getUnpaidOrders();
        assertFalse(unpaid.contains(order1));
    }

    @Test
    void shouldReturnRemainingPaymentMethods() {
        List<PaymentMethod> remaining = bill.getRemainingPaymentMethods();
        assertEquals(2, remaining.size());
    }

    @Test
    void shouldIncludePaymentInBillResult() {
        bill.addPayment(order1, method1, new BigDecimal("100.00"));
        BillResult result = bill.getBillResult();
        assertEquals(1, result.getPayments().size());
    }
}
