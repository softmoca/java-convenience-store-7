package store.domain;

public class Product {
    private final String name;
    private final int price;
    private int promotionStock;
    private int regularStock;

    public Product(String name, int price, int promotionStock, int regularStock) {
        this.name = name;
        this.price = price;
        this.promotionStock = promotionStock;
        this.regularStock = regularStock;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getTotalStock() {
        return promotionStock + regularStock;
    }

    public boolean canPurchase(int quantity) {
        return quantity <= getTotalStock();
    }

}
