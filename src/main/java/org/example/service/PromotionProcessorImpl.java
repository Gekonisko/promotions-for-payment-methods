package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PromotionProcessorImpl implements PromotionProcessor {

    @Override
    public BigDecimal process(Order order, PaymentMethod paymentMethod, BigDecimal paymentAmount, BigDecimal alreadyPaidAmount) {
        // Rabaty z używania Punktów
        if(paymentMethod.id.equals("PUNKTY")) {
            if(paymentAmount.compareTo(BigDecimal.ZERO) == 0 &&
                    paymentAmount.compareTo(order.value) == 0)
                return order.value
                        .multiply(BigDecimal.valueOf((100 - paymentMethod.discount)))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            var paidPercentage = paymentAmount
                    .multiply(paymentAmount).multiply(BigDecimal.valueOf(100))
                    .divide(order.value, 2, RoundingMode.HALF_UP);

            if (paidPercentage.compareTo(BigDecimal.valueOf(10)) >= 0)
                return order.value
                        .multiply(BigDecimal.valueOf((90)))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        // Rabaty z używania karty banku
        else if(order.promotions.contains(paymentMethod.id) &&
                alreadyPaidAmount.equals(BigDecimal.ZERO) &&
                paymentAmount.compareTo(order.value) == 0) {
            return order.value
                    .multiply(BigDecimal.valueOf((100 - paymentMethod.discount)))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return paymentAmount;
    }

}
