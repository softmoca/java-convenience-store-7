package store.domain.service;

import java.util.Map;
import store.domain.entity.Product;
import store.domain.vo.PurchaseResult;

public class MembershipCalculator {
    private static final double DISCOUNT_RATE = 0.3;
    private static final int MAX_DISCOUNT = 8000;

    private MembershipCalculator() {
    }

    public static int calculateDiscount(Map<Product, PurchaseResult> purchases) {
        int eligibleAmount = calculateEligibleAmount(purchases);
        return applyDiscountLimit(eligibleAmount);
    }

    private static int applyDiscountLimit(int eligibleAmount) {
        int discount = (int) (eligibleAmount * DISCOUNT_RATE);
        return Math.min(discount, MAX_DISCOUNT);
    }

    private static int calculateEligibleAmount(Map<Product, PurchaseResult> purchases) {
        return purchases.entrySet().stream()
                .mapToInt(entry -> calculateSingleProductEligibleAmount(
                        entry.getKey(), entry.getValue()
                ))
                .sum();
    }

    private static int calculateSingleProductEligibleAmount(Product product, PurchaseResult result) {
        if (hasNoPromotion(result)) {
            return calculateNonPromotionAmount(product, result);
        }

        if (requiresPartialFullPrice(result)) {
            return calculateFullPriceAmount(product, result);
        }

        return 0;
    }

    private static boolean hasNoPromotion(PurchaseResult result) {
        return result.getFreeQuantity() == 0;
    }

    private static boolean requiresPartialFullPrice(PurchaseResult result) {
        return result.requiresFullPrice();
    }

    private static int calculateNonPromotionAmount(Product product, PurchaseResult result) {
        return product.getPrice() * result.getPayQuantity();
    }

    private static int calculateFullPriceAmount(Product product, PurchaseResult result) {
        return product.getPrice() * result.getFullPriceQuantity();
    }

}
