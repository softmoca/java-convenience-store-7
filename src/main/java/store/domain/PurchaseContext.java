package store.domain;


import java.util.ArrayList;
import java.util.List;

public class PurchaseContext {
    private final List<PurchaseDetail> details = new ArrayList<>();
    private int totalPayAmount = 0;
    private int promotionDiscount = 0;
    private int membershipDiscount = 0;

    public void addPurchase(Product product, PurchaseResult result) {
        details.add(new PurchaseDetail(product, result));

        int originalAmount = product.getPrice() * result.getTotalQuantity();
        int discountAmount = product.getPrice() * result.getFreeQuantity();

        totalPayAmount += (originalAmount - discountAmount);
        promotionDiscount += discountAmount;
    }


    public int getTotalPayAmount() {
        return totalPayAmount - membershipDiscount;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    static class PurchaseDetail {
        final Product product;
        final PurchaseResult result;

        PurchaseDetail(Product product, PurchaseResult result) {
            this.product = product;
            this.result = result;
        }
    }
    public List<PurchaseDetail> getDetails() {
        return new ArrayList<>(details);
    }

}
