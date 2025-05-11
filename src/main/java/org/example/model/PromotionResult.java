package org.example.model;

import java.math.BigDecimal;

public class PromotionResult {
    BigDecimal finalAmount;
    BigDecimal discountAmount;

    public PromotionResult(BigDecimal finalAmount, BigDecimal discountAmount) {
        this.finalAmount = finalAmount;
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
}
