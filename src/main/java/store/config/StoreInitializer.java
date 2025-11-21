package store.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import store.domain.aggregate.ConvenienceStore;
import store.domain.entity.Product;
import store.domain.vo.Promotion;
import store.infrastructure.FileParser;

public final class StoreInitializer {
    private static final String PROMOTIONS_FILE = "src/main/resources/promotions.md";
    private static final String PRODUCTS_FILE = "src/main/resources/products.md";

    private StoreInitializer() {
    }

    public static ConvenienceStore initialize() {
        try {
            String promotionsContent = Files.readString(Paths.get(PROMOTIONS_FILE));
            String productsContent = Files.readString(Paths.get(PRODUCTS_FILE));

            FileParser parser = new FileParser();
            List<Promotion> promotions = parser.parsePromotions(promotionsContent);

            Map<String, Promotion> promotionMap = promotions.stream()
                    .collect(Collectors.toMap(Promotion::getName, p -> p));

            Map<String, Product> products = parser.parseProducts(productsContent, promotionMap);

            return new ConvenienceStore(products);

        } catch (IOException e) {
            throw new IllegalStateException("상품 정보를 불러올 수 없습니다.", e);
        }
    }
}
