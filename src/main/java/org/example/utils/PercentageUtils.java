package org.example.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageUtils {
    public static BigDecimal applyDiscount(BigDecimal value, int discountPercent) {
        return value
                .multiply(BigDecimal.valueOf(100 - discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateDiscountAmount(BigDecimal value, int percentage) {
        return value.subtract(applyDiscount(value, percentage));
    }

    public static BigDecimal calculatePercentage(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return part
                .multiply(BigDecimal.valueOf(100))
                .divide(total, 2, RoundingMode.HALF_UP);
    }


}
