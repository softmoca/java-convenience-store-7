package store.domain;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvenienceStore {
    private final Map<String, Product> products;
    private final PurchaseCalculator calculator;

    public ConvenienceStore(Map<String, Product> products) {
        this.products = new HashMap<>(products);
        this.calculator = new PurchaseCalculator();
    }

    public PurchaseContext processPurchase(List<OrderItem> items, LocalDate date) {
        PurchaseContext context = new PurchaseContext();

        for (OrderItem item : items) {
            Product product = products.get(item.getProductName());

            if (!product.canPurchase(item.getQuantity())) {
                throw new IllegalArgumentException(
                        "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."
                );
            }

            PurchaseResult result = calculator.calculate(
                    product, item.getQuantity(), date
            );

            context.addPurchase(product, result);
        }

        return context;
    }
}
