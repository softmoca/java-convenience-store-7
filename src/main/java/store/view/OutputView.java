package store.view;


import java.util.Comparator;
import java.util.Map;
import store.domain.Product;
import store.domain.Receipt;

public class OutputView {

    public void printWelcome() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.");
        System.out.println();
    }

    public void printProducts(Map<String, Product> products) {
        products.values().stream()
                .sorted(Comparator.comparing(Product::getName))
                .forEach(this::printProduct);
        System.out.println();
    }

    private void printProduct(Product product) {
        if (product.hasPromotion() && product.getPromotionStock() > 0) {
            System.out.printf("- %s %,d원 %d개 %s%n",
                    product.getName(),
                    product.getPrice(),
                    product.getPromotionStock(),
                    product.getPromotion().getName()
            );
        } else if (product.hasPromotion()) {
            System.out.printf("- %s %,d원 재고 없음 %s%n",
                    product.getName(),
                    product.getPrice(),
                    product.getPromotion().getName()
            );
        }

        // 일반 재고 출력
        if (product.getRegularStock() > 0) {
            System.out.printf("- %s %,d원 %d개%n",
                    product.getName(),
                    product.getPrice(),
                    product.getRegularStock()
            );
        } else if (!product.hasPromotion() || product.getRegularStock() == 0) {
            System.out.printf("- %s %,d원 재고 없음%n",
                    product.getName(),
                    product.getPrice()
            );
        }
    }

    public void printReceipt(Receipt receipt) {
        System.out.println("===========W 편의점=============");
        System.out.println("상품명\t\t수량\t금액");

        // 구매 내역
        receipt.getPurchaseItems().forEach(item -> {
            System.out.printf("%s\t\t%d\t%,d%n",
                    item.getName(),
                    item.getQuantity(),
                    item.getAmount()
            );
        });

        // 증정 내역
        if (!receipt.getFreeItems().isEmpty()) {
            System.out.println("===========증\t정=============");
            receipt.getFreeItems().forEach(item -> {
                System.out.printf("%s\t\t%d%n",
                        item.getName(),
                        item.getQuantity()
                );
            });
        }

        // 금액 정보
        System.out.println("==============================");
        System.out.printf("총구매액\t\t%d\t%,d%n",
                receipt.getTotalQuantity(),
                receipt.getTotalAmount()
        );
        System.out.printf("행사할인\t\t\t-%,d%n", receipt.getPromotionDiscount());
        System.out.printf("멤버십할인\t\t\t-%,d%n", receipt.getMembershipDiscount());
        System.out.printf("내실돈\t\t\t %,d%n", receipt.getFinalAmount());
        System.out.println();
    }
}
