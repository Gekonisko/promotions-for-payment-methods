package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PromotionResult;

import java.math.BigDecimal;

public interface PromotionProcessor {
    PromotionResult process(Order order, PaymentMethod paymentMethod, BigDecimal paymentAmount, BigDecimal paidAmount);

}

