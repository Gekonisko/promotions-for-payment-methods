package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PaymentResult;

import java.util.List;


public interface Bill {

    List<Order> getOrders();
    List<PaymentMethod> getPaymentMethods();
    void addPayment(Order order, PaymentMethod paymentMethod);
    List<PaymentResult> getBillResult ();
}
