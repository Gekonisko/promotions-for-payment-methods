package org.example.service;

import org.example.model.PaymentResult;

import java.util.List;

public interface PaymentCalculator {
    List<PaymentResult> calculate(Bill bill);
}
