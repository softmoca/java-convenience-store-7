package store.domain;


public class PurchaseResult {
    private final int payQuantity;
    private final int freeQuantity;

    public PurchaseResult(int payQuantity, int freeQuantity) {
        this.payQuantity = payQuantity;
        this.freeQuantity = freeQuantity;
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
}
