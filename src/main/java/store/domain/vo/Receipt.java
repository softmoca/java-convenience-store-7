package store.domain.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import store.domain.aggregate.PurchaseContext;
import store.domain.entity.Product;

public class Receipt {
    private final List<LineItem> purchaseItems = new ArrayList<>();
    private final List<LineItem> freeItems = new ArrayList<>();
    private final int totalAmount;
    private final int promotionDiscount;
    private final int membershipDiscount;
    private final int finalAmount;

    public Receipt(PurchaseContext context) {
        initializeItems(context);
        this.totalAmount = calculateTotalAmount();
        this.promotionDiscount = context.calculatePromotionDiscount();
        this.membershipDiscount = context.calculateMembershipDiscount();
        this.finalAmount = context.calculateFinalAmount();
    }

    private void initializeItems(PurchaseContext context) {
        for (Map.Entry<Product, PurchaseResult> entry : context.getPurchases().entrySet()) {
            addItemsFromEntry(entry.getKey(), entry.getValue());
        }
    }

    private void addItemsFromEntry(Product product, PurchaseResult result) {
        addPurchaseItem(product, result);
        addFreeItem(product, result);
    }

    private void addPurchaseItem(Product product, PurchaseResult result) {
        int totalQuantity = result.getTotalQuantity();

        if (totalQuantity <= 0) {
            return;
        }

        purchaseItems.add(new LineItem(
                product.getName(),
                totalQuantity,
                product.getPrice() * totalQuantity
        ));
    }

    private void addFreeItem(Product product, PurchaseResult result) {
        if (result.getFreeQuantity() <= 0) {
            return;
        }

        freeItems.add(new LineItem(
                product.getName(),
                result.getFreeQuantity(),
                0
        ));
    }


    private int calculateTotalAmount() {
        return purchaseItems.stream()
                .mapToInt(LineItem::getAmount)
                .sum();
    }

    public int getTotalQuantity() {
        return purchaseItems.stream()
                .mapToInt(LineItem::getQuantity)
                .sum();
    }

    public static class LineItem {
        private final String name;
        private final int quantity;
        private final int amount;

        public LineItem(String name, int quantity, int amount) {
            this.name = name;
            this.quantity = quantity;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getAmount() {
            return amount;
        }
    }

    public List<LineItem> getPurchaseItems() {
        return purchaseItems;
    }

    public List<LineItem> getFreeItems() {
        return freeItems;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }

    public int getFinalAmount() {
        return finalAmount;
    }
}
