package store.domain;

import java.time.LocalDate;

public class PurchaseCalculator {

    public PurchaseResult calculate(Product product, int requestQuantity, LocalDate date) {

        // 프로모션이 없거나 기간이 아니면 일반 구매
        if (!product.hasPromotion() || !product.getPromotion().isAvailable(date)) {
            return new PurchaseResult.Builder(product.getName(), requestQuantity)
                    .payQuantity(requestQuantity)
                    .freeQuantity(0)
                    .build();
        }

        Promotion promotion = product.getPromotion();
        int promotionStock = product.getPromotionStock();

        //  1단계 : 추가 구매 제안 체크
        PurchaseResult suggestionResult = checkSuggestion(
                product, requestQuantity, promotion, promotionStock
        );
        if (suggestionResult != null) {
            return suggestionResult;
        }


        // 2단계 : 정상 프로모션 적용
        return applyNormalPromotion(product, requestQuantity, promotion);
    }

    private PurchaseResult checkSuggestion(Product product, int quantity,
                                           Promotion promotion, int promotionStock) {
        int setSize = promotion.getBuy() + promotion.getGet();
        int remainder = quantity % setSize;


        if (remainder == promotion.getBuy()) {        // 딱 buy개만 구매하는 경우
            int totalNeeded = quantity + promotion.getGet(); // get개를 추가하면 한 세트 완성
            if (promotionStock >= totalNeeded) {
                return new PurchaseResult.Builder(product.getName(), quantity)
                        .payQuantity(quantity)  // 일단 원래 수량대로 사용자 응답 로직  TODO
                        .freeQuantity(0)
                        .suggestAddition(promotion.getGet()) // N개 추가 구매 제안
                        .build();
            }
        }

        return null;
    }


    private PurchaseResult applyNormalPromotion(Product product, int quantity,
                                                Promotion promotion) {
        int freeCount = promotion.calculateFreeCount(quantity);

        return new PurchaseResult.Builder(product.getName(), quantity)
                .payQuantity(quantity - freeCount)
                .freeQuantity(freeCount)
                .build();
    }


}
