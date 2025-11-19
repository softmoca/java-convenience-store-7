package store.domain;


public class PurchaseResult {
    private final String productName;
    private final int payQuantity;
    private final int freeQuantity;
    private final boolean suggestAddition;
    private final int suggestedAddition;


    public static class Builder {
        private String productName;
        private int requestedQuantity;
        private int payQuantity;
        private int freeQuantity;
        private boolean suggestAddition = false;
        private int suggestedAddition = 0;

        public Builder(String productName, int requestedQuantity) {
            this.productName = productName;
            this.requestedQuantity = requestedQuantity;
        }

        public Builder payQuantity(int quantity) {
            this.payQuantity = quantity;
            return this;
        }

        public Builder freeQuantity(int quantity) {
            this.freeQuantity = quantity;
            return this;
        }

        public Builder suggestAddition(int quantity) {
            this.suggestAddition = true;
            this.suggestedAddition = quantity;
            return this;
        }


        public PurchaseResult build() {
            return new PurchaseResult(this);
        }
    }

    private PurchaseResult(Builder builder) {
        this.productName = builder.productName;
        this.payQuantity = builder.payQuantity;
        this.freeQuantity = builder.freeQuantity;
        this.suggestAddition = builder.suggestAddition;
        this.suggestedAddition = builder.suggestedAddition;

    }


    public String getSuggestedMessage() {
        if (!suggestAddition) return null;
        return String.format(
                "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)",
                productName, suggestedAddition
        );
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
