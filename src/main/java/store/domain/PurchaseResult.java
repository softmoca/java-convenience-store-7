package store.domain;


public class PurchaseResult {
    private final int payQuantity;
    private final int freeQuantity;
    private final boolean suggestAddition;
    private final int suggestedAddition;

    private PurchaseResult(int payQuantity, int freeQuantity,
                           boolean suggestAddition, int suggestedAddition   ) {
        this.payQuantity = payQuantity;
        this.freeQuantity = freeQuantity;
        this.suggestAddition = suggestAddition;
        this.suggestedAddition = suggestedAddition;
    }

    public static PurchaseResult suggestAddition(int currentQuantity, int addition) {
        return new PurchaseResult(currentQuantity, 0, true, addition);
    }

    public static PurchaseResult normal(int payQuantity, int freeQuantity) {
        return new PurchaseResult(payQuantity, freeQuantity, false, 0);
    }


    public int getPayQuantity() {
        return payQuantity;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }

    public int getTotalQuantity() {
        return payQuantity + freeQuantity;
    }

    public boolean shouldSuggestAddition() {
        return suggestAddition;
    }

    public int getSuggestedAddition() {
        return suggestedAddition;
    }
}
