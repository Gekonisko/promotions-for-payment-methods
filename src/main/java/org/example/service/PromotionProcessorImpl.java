package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PromotionResult;
import org.example.utils.PercentageUtils;

import java.math.BigDecimal;

public class PromotionProcessorImpl implements PromotionProcessor {

    @Override
    public PromotionResult process(Order order, PaymentMethod paymentMethod, BigDecimal paymentAmount, BigDecimal paidAmount) {
        // Rabaty z używania Punktów
        if(paymentMethod.getId().equals("PUNKTY")) {
            if(paidAmount.compareTo(BigDecimal.ZERO) == 0 &&
                    paymentAmount.compareTo(order.getValue()) == 0)

                return getPromotion(order.getValue(), paymentAmount, paymentMethod.getDiscount());

            var paidPercentage = PercentageUtils.calculatePercentage(paymentAmount, order.getValue());
            if (paidPercentage.compareTo(BigDecimal.valueOf(10)) >= 0)
                return getPromotion(order.getValue(), paymentAmount, 10);
        }
        // Rabaty z używania karty banku
        else if(order.getPromotions() != null &&
                order.getPromotions().contains(paymentMethod.getId()) &&
                paidAmount.equals(BigDecimal.ZERO) &&
                paymentAmount.compareTo(order.getValue()) == 0) {
            return getPromotion(order.getValue(), paymentAmount, paymentMethod.getDiscount());
        }
        return new PromotionResult(paymentAmount,BigDecimal.valueOf(0));
    }

    private PromotionResult getPromotion(BigDecimal orderValue, BigDecimal paymentAmount, int discount) {
        var orderAfterPromotion = PercentageUtils.applyDiscount(orderValue, discount);
        return new PromotionResult(paymentAmount.min(orderAfterPromotion), orderValue.subtract(orderAfterPromotion));
    }

}
