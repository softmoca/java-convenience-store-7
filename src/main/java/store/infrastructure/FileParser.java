package store.infrastructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.domain.Product;
import store.domain.Promotion;

public class FileParser {

    public List<Promotion> parsePromotions(String content) {
        List<Promotion> promotions = new ArrayList<>();
        String[] lines = content.split("\n");

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");
            promotions.add(new Promotion(
                    parts[0],  // name
                    Integer.parseInt(parts[1]),  // buy
                    Integer.parseInt(parts[2]),  // get
                    LocalDate.parse(parts[3]),   // startDate
                    LocalDate.parse(parts[4])    // endDate
            ));
        }

        return promotions;
    }

    public Map<String, Product> parseProducts(String content, Map<String, Promotion> promotionMap) {
        Map<String, Product> products = new LinkedHashMap<>();
        String[] lines = content.split("\n");

        Map<String, ProductBuilder> builders = new LinkedHashMap<>();

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int quantity = Integer.parseInt(parts[2]);
            String promotionName = parts[3];

            // 이미 있는 상품이면 재고만 추가
            ProductBuilder builder = builders.computeIfAbsent(name,
                    k -> new ProductBuilder(name, price));

            if (!"null".equals(promotionName)) {         // 프로모션 재고
                builder.setPromotionStock(quantity);
                builder.setPromotion(promotionMap.get(promotionName));
            } else {            // 일반 재고
                builder.setRegularStock(quantity);
            }
        }

        builders.forEach((name, builder) -> {
            products.put(name, builder.build());
        });

        return products;
    }

    private static class ProductBuilder {
        private final String name;
        private final int price;
        private int promotionStock = 0;
        private int regularStock = 0;
        private Promotion promotion = null;

        ProductBuilder(String name, int price) {
            this.name = name;
            this.price = price;
        }

        void setPromotionStock(int stock) {
            this.promotionStock = stock;
        }

        void setRegularStock(int stock) {
            this.regularStock = stock;
        }

        void setPromotion(Promotion promotion) {
            this.promotion = promotion;
        }

        Product build() {
            if (promotion != null) {
                return new Product(name, price, promotionStock, regularStock, promotion);
            }
            return new Product(name, price, promotionStock, regularStock);
        }
    }

}
