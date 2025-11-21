package store.domain.vo;


public class OrderItem {
    private final String productName;
    private final int quantity;

    public OrderItem(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public static OrderItem parse(String input) {
        String cleaned = input.replace("[", "").replace("]", "");
        String[] parts = cleaned.split("-");
        return new OrderItem(parts[0], Integer.parseInt(parts[1]));
    }
}
