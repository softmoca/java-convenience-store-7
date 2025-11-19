package store.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Receipt {
    private final List<LineItem> purchaseItems = new ArrayList<>();
    private final List<LineItem> freeItems = new ArrayList<>();
    private final int totalAmount;
    private final int promotionDiscount;
    private final int membershipDiscount;
    private final int finalAmount;

    public Receipt(PurchaseContext context) {
        for (Map.Entry<Product, PurchaseResult> entry : context.getPurchases().entrySet()) {
            Product product = entry.getKey();
            PurchaseResult result = entry.getValue();

            // 구매 항목 (전체 수량 표시)
            int totalQuantity = result.getPayQuantity() + result.getFreeQuantity();
            if (totalQuantity > 0) {
                purchaseItems.add(new LineItem(
                        product.getName(),
                        totalQuantity,
                        product.getPrice() * totalQuantity
                ));
            }

            // 증정 항목
            if (result.getFreeQuantity() > 0) {
                freeItems.add(new LineItem(
                        product.getName(),
                        result.getFreeQuantity(),
                        0  // 증정품은 금액 표시 안함
                ));
            }
        }

        // 금액 계산
        this.totalAmount = calculateTotalAmount();
        this.promotionDiscount = context.calculatePromotionDiscount();
        this.membershipDiscount = context.calculateMembershipDiscount();
        this.finalAmount = context.calculateFinalAmount();
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

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public int getAmount() { return amount; }
    }

    public List<LineItem> getPurchaseItems() { return purchaseItems; }
    public List<LineItem> getFreeItems() { return freeItems; }
    public int getTotalAmount() { return totalAmount; }
    public int getPromotionDiscount() { return promotionDiscount; }
    public int getMembershipDiscount() { return membershipDiscount; }
    public int getFinalAmount() { return finalAmount; }
}
