package store.domain.entity;

import store.domain.vo.Promotion;

public class Product {
    private final String name;
    private final int price;
    private int promotionStock;
    private int regularStock;
    private final Promotion promotion;

    public Product(String name, int price, int promotionStock,
                   int regularStock, Promotion promotion) {
        this.name = name;
        this.price = price;
        this.promotionStock = promotionStock;
        this.regularStock = regularStock;
        this.promotion = promotion;
    }

    public Product(String name, int price, int promotionStock, int regularStock) {
        this(name, price, promotionStock, regularStock, null);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getTotalStock() {
        return promotionStock + regularStock;
    }

    public boolean canPurchase(int quantity) {
        return quantity <= getTotalStock();
    }

    public boolean hasPromotion() {
        return promotion != null;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public int getPromotionStock() {
        return promotionStock;
    }

    public int getRegularStock() {
        return regularStock;
    }

    public void deductStock(int quantity) {
        if (canDeductFromPromotionOnly(quantity)) {
            deductFromPromotionStock(quantity);
            return;
        }

        deductFromBothStocks(quantity);
    }

    private boolean canDeductFromPromotionOnly(int quantity) {
        return promotionStock >= quantity;
    }

    private void deductFromPromotionStock(int quantity) {
        promotionStock -= quantity;
    }

    private void deductFromBothStocks(int quantity) {
        int remainingAfterPromotion = calculateRemainingQuantity(quantity);
        promotionStock = 0;
        regularStock -= remainingAfterPromotion;
    }

    private int calculateRemainingQuantity(int quantity) {
        return quantity - promotionStock;
    }
}
