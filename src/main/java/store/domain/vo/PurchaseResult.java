package store.domain.vo;


public class PurchaseResult {
    private final String productName;
    private final int requestedQuantity;        // 원래 요청한 수량
    private final int payQuantity;              // 실제 결제할 수량
    private final int freeQuantity;             // 무료로 받는 수량
    private final boolean suggestAddition;      // 추가 구매 제안 필요 여부
    private final int suggestedAddition;        // 추가 구매 제안 수량
    private final boolean requiresFullPrice;    // 정가 결제 확인 필요 여부
    private final int fullPriceQuantity;        // 정가로 결제해야 할 수량


    public static class Builder {
        // 필수 파라미터
        private String productName;
        private int requestedQuantity;

        // 선택적 파라미터
        private int payQuantity;
        private int freeQuantity;
        private boolean suggestAddition = false;
        private int suggestedAddition = 0;
        private boolean requiresFullPrice = false;
        private int fullPriceQuantity = 0;

        // 필수 파라미터만 받는 생성자
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

        // 추가 구매 제안 설정
        public Builder suggestAddition(int quantity) {
            this.suggestAddition = true;
            this.suggestedAddition = quantity; // 제안할 수량
            return this;
        }

        // 정가 결제 필요 설정 (프로모션 재고 부족 시)
        public Builder requiresFullPrice(int quantity) {
            this.requiresFullPrice = true;
            this.fullPriceQuantity = quantity; // 정가로 구매할 수량
            return this;
        }

        public PurchaseResult build() {
            return new PurchaseResult(this);
        }
    }

    private PurchaseResult(Builder builder) {
        this.productName = builder.productName;
        this.requestedQuantity = builder.requestedQuantity;
        this.payQuantity = builder.payQuantity;
        this.freeQuantity = builder.freeQuantity;
        this.suggestAddition = builder.suggestAddition;
        this.suggestedAddition = builder.suggestedAddition;
        this.requiresFullPrice = builder.requiresFullPrice;
        this.fullPriceQuantity = builder.fullPriceQuantity;
    }

    public String getSuggestedMessage() {
        if (!suggestAddition) {
            return null;
        }
        return String.format(
                "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)",
                productName, suggestedAddition
        );
    }

    public String getFullPriceMessage() {
        if (!requiresFullPrice) {
            return null;
        }
        return String.format(
                "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)",
                productName, fullPriceQuantity
        );
    }

    public PurchaseResult acceptAddition() {
        if (!suggestAddition) {
            return this;  // 제안이 없었으면 그대로 반환
        }

        int newQuantity = requestedQuantity + suggestedAddition;  // 총 수량 증가
        return new Builder(productName, newQuantity)
                .payQuantity(payQuantity)                            // 결제 수량은 그대로
                .freeQuantity(freeQuantity + suggestedAddition)      // 무료 수량 증가
                .build();
    }

    public PurchaseResult rejectFullPrice() {
        if (!requiresFullPrice) {
            return this;  // 정가 결제가 없었으면 그대로 반환
        }

        int newQuantity = requestedQuantity - fullPriceQuantity;  // 정가 부분 제외
        return new Builder(productName, newQuantity)
                .payQuantity(payQuantity - fullPriceQuantity)        // 결제 수량 감소
                .freeQuantity(freeQuantity)                          // 무료 수량은 그대로
                .build();
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

    public boolean requiresFullPrice() {
        return requiresFullPrice;
    }

    public int getFullPriceQuantity() {
        return fullPriceQuantity;
    }
}
