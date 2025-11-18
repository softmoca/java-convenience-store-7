package store.domain;

import java.time.LocalDate;

public class PurchaseCalculator {

    public PurchaseResult calculate(Product product, int requestQuantity, LocalDate date) {

        if (!product.hasPromotion() || !product.getPromotion().isAvailable(date)) {
            return PurchaseResult.normal(requestQuantity, 0);
        }

        Promotion promotion = product.getPromotion();
        int promotionStock = product.getPromotionStock();

        int setSize = promotion.getBuy() + promotion.getGet();
        int remainder = requestQuantity % setSize;

        if (remainder > 0 && remainder == promotion.getBuy()) {
            // 딱 1개 더 사면 무료 혜택
            if (promotionStock >= requestQuantity + promotion.getGet()) {
                return PurchaseResult.suggestAddition(requestQuantity, promotion.getGet());
            }
        }


        if (requestQuantity <= promotionStock) {       // 프로모션 재고 충분
            int freeCount = promotion.calculateFreeCount(requestQuantity);
            return PurchaseResult.normal(requestQuantity - freeCount, freeCount);
        }

        //  프로모션 재고 부족 케이스 TODO
        return PurchaseResult.normal(requestQuantity, 0);
    }
}
