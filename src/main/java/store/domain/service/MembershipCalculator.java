package store.domain.service;

import java.util.Map;
import store.domain.entity.Product;
import store.domain.vo.PurchaseResult;

public class MembershipCalculator {
    private static final double DISCOUNT_RATE = 0.3;
    private static final int MAX_DISCOUNT = 8000;

    public int calculateDiscount(Map<Product, PurchaseResult> purchases) {
        int eligibleAmount = calculateEligibleAmount(purchases);
        int discount = (int) (eligibleAmount * DISCOUNT_RATE);

        return Math.min(discount, MAX_DISCOUNT);
    }

    private int calculateEligibleAmount(Map<Product, PurchaseResult> purchases) {
        int total = 0;

        for (Map.Entry<Product, PurchaseResult> entry : purchases.entrySet()) {
            Product product = entry.getKey();
            PurchaseResult result = entry.getValue();

            if (result.getFreeQuantity() == 0) {
                // 프로모션이 없는 상품
                total += product.getPrice() * result.getPayQuantity();
            } else if (result.requiresFullPrice()) {
                // 프로모션 재고 부족으로 정가 결제하는 부분
                total += product.getPrice() * result.getFullPriceQuantity();
            }
        }

        return total;
    }
}
