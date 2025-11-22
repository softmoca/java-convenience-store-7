package store.domain.aggregate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import store.domain.entity.Product;
import store.domain.service.MembershipCalculator;
import store.domain.vo.PurchaseResult;

public class PurchaseContext {
    private final Map<Product, PurchaseResult> purchases = new LinkedHashMap<>();
    private boolean membershipApplied = false;

    public void addPurchase(Product product, PurchaseResult result) {
        purchases.put(product, result);
    }

    public void applyMembershipDiscount(boolean apply) {
        this.membershipApplied = apply;
    }

    public int calculateTotalAmount() {
        int total = 0;
        for (Map.Entry<Product, PurchaseResult> entry : purchases.entrySet()) {
            Product product = entry.getKey();
            PurchaseResult result = entry.getValue();

            total += product.getPrice() * result.getPayQuantity();
        }
        return total;
    }

    public int calculatePromotionDiscount() {
        int discount = 0;
        for (Map.Entry<Product, PurchaseResult> entry : purchases.entrySet()) {
            Product product = entry.getKey();
            PurchaseResult result = entry.getValue();

            discount += product.getPrice() * result.getFreeQuantity();
        }
        return discount;
    }

    public int calculateMembershipDiscount() {
        if (!membershipApplied) {
            return 0;
        }
        return MembershipCalculator.calculateDiscount(purchases);
    }

    public int calculateFinalAmount() {
        return calculateTotalAmount() - calculateMembershipDiscount();
    }

    public Map<Product, PurchaseResult> getPurchases() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(purchases));
    }

    public void updatePurchase(Product product, PurchaseResult newResult) {
        purchases.put(product, newResult);
    }

}
