package org.example.service;

import org.example.model.BillResult;

public interface PaymentCalculator {
    BillResult calculate(Bill bill);
}
