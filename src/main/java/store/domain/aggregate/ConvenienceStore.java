package store.domain.aggregate;


import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.constant.ErrorMessage;
import store.domain.entity.Product;
import store.domain.service.PurchaseCalculator;
import store.domain.vo.OrderItem;
import store.domain.vo.PurchaseResult;

public class ConvenienceStore {
    private final Map<String, Product> products;

    public ConvenienceStore(Map<String, Product> products) {
        this.products = new LinkedHashMap<>(products);
    }

    public PurchaseContext processPurchase(List<OrderItem> items, LocalDate date) {
        validateProducts(items);
        return createPurchaseContext(items, date);
    }

    private PurchaseContext createPurchaseContext(List<OrderItem> items, LocalDate date) {
        PurchaseContext context = new PurchaseContext();

        for (OrderItem item : items) {
            processSingleItem(context, item, date);
        }

        return context;
    }

    private void processSingleItem(PurchaseContext context, OrderItem item, LocalDate date) {
        Product product = products.get(item.getProductName());
        validateStock(product, item.getQuantity());

        PurchaseResult result = PurchaseCalculator.calculate(product, item.getQuantity(), date);
        context.addPurchase(product, result);
    }

    private void validateStock(Product product, int quantity) {
        if (!product.canPurchase(quantity)) {
            throw new IllegalArgumentException(ErrorMessage.EXCEED_STOCK.getMessage());
        }
    }

    public void updateStock(PurchaseContext context) {
        for (Map.Entry<Product, PurchaseResult> entry : context.getPurchases().entrySet()) {
            Product product = products.get(entry.getKey().getName());
            product.deductStock(entry.getValue().getTotalQuantity());
        }
    }


    public Map<String, Product> getProducts() {
        return Collections.unmodifiableMap(products);
    }


    public void validateProducts(List<OrderItem> items) {
        for (OrderItem item : items) {
            validateProductExists(item);
        }
    }

    private void validateProductExists(OrderItem item) {
        if (!products.containsKey(item.getProductName())) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.getMessage());
        }
    }

}
