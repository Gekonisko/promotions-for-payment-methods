package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;

import java.math.BigDecimal;

public interface PromotionProcessor {
    BigDecimal process(Order order, PaymentMethod paymentMethod, BigDecimal amountToPay, BigDecimal alreadyPaidAmount);

}

