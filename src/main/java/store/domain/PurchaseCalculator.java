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

        // 2단계 : 프로모션 재고 부족 체크
        PurchaseResult fullPriceResult = checkFullPriceRequired(
                product, requestQuantity, promotion, promotionStock
        );
        if (fullPriceResult != null) {
            return fullPriceResult;
        }


        // 3단계 : 정상 프로모션 적용
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

    private PurchaseResult checkFullPriceRequired(Product product, int quantity,
                                                  Promotion promotion, int promotionStock) {
        if (quantity > promotionStock) {      // 구매 희망 수량이 프로모션 재고보다 많은 경우
            int applicableQuantity = promotion.getApplicableQuantity(promotionStock);
            int fullPriceQuantity = quantity - applicableQuantity;       // 정가로 구매해야 할 수량

            // 프로모션 적용 부분의 무료 개수 계산
            int freeInApplicable = promotion.calculateFreeCount(applicableQuantity);
            // 프로모션 적용 부분의 실제 결제 개수
            int payInApplicable = applicableQuantity - freeInApplicable;

            return new PurchaseResult.Builder(product.getName(), quantity)
                    .payQuantity(payInApplicable + fullPriceQuantity) // 프로모션 결제 + 정가 결제
                    .freeQuantity(freeInApplicable) // 프로모션으로 받은 무료 개수
                    .requiresFullPrice(fullPriceQuantity)    // 정가 결제 필요 수량 (사용자 확인용)
                    .build();
        }

        return null;  // 정가 결제 필요 없음
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
