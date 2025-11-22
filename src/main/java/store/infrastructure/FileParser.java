package store.infrastructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.domain.entity.Product;
import store.domain.vo.Promotion;

public final class FileParser {
    private FileParser() {
    }

    public static List<Promotion> parsePromotions(String content) {
        String[] lines = content.split("\n");
        return parsePromotionLines(lines);
    }

    private static List<Promotion> parsePromotionLines(String[] lines) {
        List<Promotion> promotions = new ArrayList<>();

        for (int i = 1; i < lines.length; i++) {
            addPromotionIfValid(lines[i], promotions);
        }

        return promotions;
    }

    private static void addPromotionIfValid(String line, List<Promotion> promotions) {
        Promotion promotion = parsePromotionLine(line);
        promotions.add(promotion);
    }

    private static Promotion parsePromotionLine(String line) {
        String[] parts = line.split(",");
        return createPromotion(parts);
    }

    private static Promotion createPromotion(String[] parts) {
        return new Promotion(
                parts[0],
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                LocalDate.parse(parts[3]),
                LocalDate.parse(parts[4])
        );
    }

    public static Map<String, Product> parseProducts(String content, Map<String, Promotion> promotionMap) {
        String[] lines = content.split("\n");
        Map<String, ProductBuilder> builders = new LinkedHashMap<>();

        parseProductLines(lines, builders, promotionMap);

        return buildProducts(builders);
    }

    private static void parseProductLines(String[] lines, Map<String, ProductBuilder> builders,
                                          Map<String, Promotion> promotionMap) {
        for (int i = 1; i < lines.length; i++) {
            processProductLine(lines[i], builders, promotionMap);
        }
    }

    private static void processProductLine(String line, Map<String, ProductBuilder> builders,
                                           Map<String, Promotion> promotionMap) {

        String[] parts = line.split(",");
        addProductData(parts, builders, promotionMap);
    }

    private static void addProductData(String[] parts, Map<String, ProductBuilder> builders,
                                       Map<String, Promotion> promotionMap) {
        String name = parts[0];
        int price = Integer.parseInt(parts[1]);
        int quantity = Integer.parseInt(parts[2]);
        String promotionName = parts[3];

        ProductBuilder builder = getOrCreateBuilder(builders, name, price);
        applyStockToBuilder(builder, quantity, promotionName, promotionMap);
    }

    private static ProductBuilder getOrCreateBuilder(Map<String, ProductBuilder> builders,
                                                     String name, int price) {
        return builders.computeIfAbsent(name, k -> new ProductBuilder(name, price));
    }

    private static void applyStockToBuilder(ProductBuilder builder, int quantity,
                                            String promotionName, Map<String, Promotion> promotionMap) {
        if (isNullPromotion(promotionName)) {
            builder.setRegularStock(quantity);
            return;
        }

        builder.setPromotionStock(quantity);
        builder.setPromotion(promotionMap.get(promotionName));
    }

    private static boolean isNullPromotion(String promotionName) {
        return "null".equals(promotionName);
    }

    private static Map<String, Product> buildProducts(Map<String, ProductBuilder> builders) {
        Map<String, Product> products = new LinkedHashMap<>();
        builders.forEach((name, builder) -> products.put(name, builder.build()));
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
            if (hasPromotion()) {
                return new Product(name, price, promotionStock, regularStock, promotion);
            }
            return new Product(name, price, promotionStock, regularStock);
        }

        private boolean hasPromotion() {
            return promotion != null;
        }
    }
}
