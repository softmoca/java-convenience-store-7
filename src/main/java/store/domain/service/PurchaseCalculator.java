package store.domain.service;

import java.time.LocalDate;
import store.domain.entity.Product;
import store.domain.vo.Promotion;
import store.domain.vo.PurchaseResult;

public class PurchaseCalculator {

    private PurchaseCalculator() {
    }

    public static PurchaseResult calculate(Product product, int requestQuantity, LocalDate date) {
        if (!product.hasPromotion() || !product.getPromotion().isAvailable(date)) {
            return createNonPromotionResult(product.getName(), requestQuantity);
        }

        return calculateWithPromotion(product, requestQuantity);
    }

    private static PurchaseResult createNonPromotionResult(String productName, int quantity) {
        return new PurchaseResult.Builder(productName, quantity)
                .payQuantity(quantity)
                .freeQuantity(0)
                .build();
    }

    private static PurchaseResult calculateWithPromotion(Product product, int requestQuantity) {
        Promotion promotion = product.getPromotion();
        int promotionStock = product.getPromotionStock();

        PurchaseResult suggestionResult = checkAdditionSuggestion(product, requestQuantity, promotion, promotionStock);
        if (suggestionResult != null) {
            return suggestionResult;
        }

        return calculatePromotionOrFullPrice(product, requestQuantity, promotion, promotionStock);
    }

    private static PurchaseResult calculatePromotionOrFullPrice(Product product, int requestQuantity,
                                                                Promotion promotion, int promotionStock) {
        PurchaseResult fullPriceResult = checkFullPriceRequired(product, requestQuantity, promotion, promotionStock);
        if (fullPriceResult != null) {
            return fullPriceResult;
        }

        return applyNormalPromotion(product, requestQuantity, promotion);
    }

    private static PurchaseResult checkAdditionSuggestion(Product product, int quantity,
                                                          Promotion promotion, int promotionStock) {
        if (!shouldSuggestAddition(quantity, promotion)) {
            return null;
        }

        if (!canAddFreeItem(quantity, promotion, promotionStock)) {
            return null;
        }

        return createAdditionSuggestionResult(product.getName(), quantity, promotion);
    }

    private static boolean shouldSuggestAddition(int quantity, Promotion promotion) {
        int setSize = promotion.getBuy() + promotion.getGet();
        int remainder = quantity % setSize;
        return remainder == promotion.getBuy();
    }

    private static boolean canAddFreeItem(int quantity, Promotion promotion, int promotionStock) {
        int totalNeeded = quantity + promotion.getGet();
        return promotionStock >= totalNeeded;
    }

    private static PurchaseResult createAdditionSuggestionResult(String productName, int quantity,
                                                                 Promotion promotion) {
        return new PurchaseResult.Builder(productName, quantity)
                .payQuantity(quantity)
                .freeQuantity(0)
                .suggestAddition(promotion.getGet())
                .build();
    }

    private static PurchaseResult checkFullPriceRequired(Product product, int quantity,
                                                         Promotion promotion, int promotionStock) {
        if (quantity <= promotionStock) {
            return null;
        }

        return createFullPriceResult(product, quantity, promotion, promotionStock);
    }

    private static PurchaseResult createFullPriceResult(Product product, int quantity,
                                                        Promotion promotion, int promotionStock) {
        int applicableQuantity = promotion.getApplicableQuantity(promotionStock);
        int fullPriceQuantity = quantity - applicableQuantity;

        int freeCount = promotion.calculateFreeCount(applicableQuantity);
        int payCount = applicableQuantity - freeCount;

        return buildFullPriceResult(product.getName(), quantity, payCount, freeCount, fullPriceQuantity);
    }


    private static PurchaseResult buildFullPriceResult(String productName, int quantity,
                                                       int payCount, int freeCount, int fullPriceQuantity) {
        return new PurchaseResult.Builder(productName, quantity)
                .payQuantity(payCount + fullPriceQuantity)
                .freeQuantity(freeCount)
                .requiresFullPrice(fullPriceQuantity)
                .build();
    }

    private static PurchaseResult applyNormalPromotion(Product product, int quantity,
                                                       Promotion promotion) {
        int freeCount = promotion.calculateFreeCount(quantity);

        return new PurchaseResult.Builder(product.getName(), quantity)
                .payQuantity(quantity - freeCount)
                .freeQuantity(freeCount)
                .build();
    }
}
