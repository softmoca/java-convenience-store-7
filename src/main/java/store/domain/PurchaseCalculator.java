package store.domain;

import java.time.LocalDate;

public class PurchaseCalculator {

    public PurchaseResult calculate(Product product, int requestQuantity, LocalDate date) {

        if (!product.hasPromotion() || !product.getPromotion().isAvailable(date)) {
            return new PurchaseResult(requestQuantity, 0);
        }

        Promotion promotion = product.getPromotion();

        int promotionStock = product.getPromotionStock();

        if (requestQuantity <= promotionStock) {// 프로모션 재고로 충분
            int freeCount = promotion.calculateFreeCount(requestQuantity);
            return new PurchaseResult(requestQuantity - freeCount, freeCount);
        }

        //  프로모션 재고 부족 케이스 TODO
        return new PurchaseResult(requestQuantity, 0);
    }
}
