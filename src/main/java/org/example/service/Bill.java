package org.example.service;

import org.example.model.BillResult;
import org.example.model.Order;
import org.example.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;


public interface Bill {

    Collection<Order> getOrders();
    Order getOrder(String orderId);
    Collection<PaymentMethod> getPaymentMethods();
    PaymentMethod getPaymentMethod(String paymentId);
    void addPayment(Order order, PaymentMethod paymentMethod, BigDecimal paymentAmount);
    BillResult getBillResult ();
    List<Order> getUnpaidOrders();
    List<PaymentMethod> getRemainingPaymentMethods();
    BigDecimal getPaidAmount(Order order);

}
