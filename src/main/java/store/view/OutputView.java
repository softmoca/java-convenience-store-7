package store.view;


import java.util.Map;
import store.domain.entity.Product;
import store.domain.vo.Receipt;

public class OutputView {

    public static void printWelcome() {
        System.out.println("안녕하세요. W편의점입니다.");
        System.out.println("현재 보유하고 있는 상품입니다.");
        System.out.println();
    }

    public static void printProducts(Map<String, Product> products) {
        products.values()
                .forEach(OutputView::printProduct);
        System.out.println();
    }


    private static void printProduct(Product product) {
        printPromotionStock(product);
        printRegularStock(product);
    }


    private static void printPromotionStock(Product product) {
        if (!product.hasPromotion()) {
            return;
        }

        if (product.getPromotionStock() > 0) {
            printPromotionStockAvailable(product);
            return;
        }

        printPromotionStockEmpty(product);
    }

    private static void printPromotionStockAvailable(Product product) {
        System.out.printf("- %s %,d원 %d개 %s%n",
                product.getName(),
                product.getPrice(),
                product.getPromotionStock(),
                product.getPromotion().getName()
        );
    }

    private static void printPromotionStockEmpty(Product product) {
        System.out.printf("- %s %,d원 재고 없음 %s%n",
                product.getName(),
                product.getPrice(),
                product.getPromotion().getName()
        );
    }


    private static void printRegularStock(Product product) {
        if (product.getRegularStock() > 0) {
            printRegularStockAvailable(product);
            return;
        }

        if (!product.hasPromotion() || product.getRegularStock() == 0) {
            printRegularStockEmpty(product);
        }
    }

    private static void printRegularStockAvailable(Product product) {
        System.out.printf("- %s %,d원 %d개%n",
                product.getName(),
                product.getPrice(),
                product.getRegularStock()
        );
    }

    private static void printRegularStockEmpty(Product product) {
        System.out.printf("- %s %,d원 재고 없음%n",
                product.getName(),
                product.getPrice()
        );
    }


    public static void printReceipt(Receipt receipt) {
        printHeader();
        printPurchaseItems(receipt);
        printFreeItems(receipt);
        printSummary(receipt);
        System.out.println();
    }

    private static void printHeader() {
        System.out.println("===========W 편의점=============");
        System.out.println("상품명\t\t수량\t금액");
    }

    private static void printPurchaseItems(Receipt receipt) {
        receipt.getPurchaseItems().forEach(item ->
                System.out.printf("%s\t\t%d\t%,d%n",
                        item.getName(),
                        item.getQuantity(),
                        item.getAmount()
                )
        );
    }

    private static void printFreeItems(Receipt receipt) {
        if (receipt.getFreeItems().isEmpty()) {
            return;
        }

        System.out.println("===========증\t정=============");
        receipt.getFreeItems().forEach(OutputView::printFreeItem);
    }

    private static void printFreeItem(Receipt.LineItem item) {
        System.out.printf("%s\t\t%d%n",
                item.getName(),
                item.getQuantity()
        );
    }

    private static void printSummary(Receipt receipt) {
        System.out.println("==============================");
        System.out.printf("총구매액\t\t%d\t%,d%n",
                receipt.getTotalQuantity(),
                receipt.getTotalAmount()
        );
        System.out.printf("행사할인\t\t\t-%,d%n", receipt.getPromotionDiscount());
        System.out.printf("멤버십할인\t\t\t-%,d%n", receipt.getMembershipDiscount());
        System.out.printf("내실돈\t\t\t %,d%n", receipt.getFinalAmount());
    }

}
