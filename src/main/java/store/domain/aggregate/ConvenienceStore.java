package store.domain.aggregate;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.domain.entity.Product;
import store.domain.service.PurchaseCalculator;
import store.domain.vo.OrderItem;
import store.domain.vo.PurchaseResult;

public class ConvenienceStore {
    private final Map<String, Product> products;
    private final PurchaseCalculator calculator;

    public ConvenienceStore(Map<String, Product> products) {
        this.products = new HashMap<>(products);
        this.calculator = new PurchaseCalculator();
    }

    public PurchaseContext processPurchase(List<OrderItem> items, LocalDate date) {
        validateProducts(items);
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

    public void updateStock(PurchaseContext context) {
        for (Map.Entry<Product, PurchaseResult> entry : context.getPurchases().entrySet()) {
            Product product = products.get(entry.getKey().getName());
            product.deductStock(entry.getValue().getTotalQuantity());
        }
    }

    public Product getProduct(String name) {
        return products.get(name);
    }

    public Map<String, Product> getProducts() {
        return products;
    }


    public void validateProducts(List<OrderItem> items) {
        for (OrderItem item : items) {
            if (!products.containsKey(item.getProductName())) {
                throw new IllegalArgumentException(
                        "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요."
                );
            }
        }
    }


}
