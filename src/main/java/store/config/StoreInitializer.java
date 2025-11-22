package store.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import store.constant.ErrorMessage;
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
            return createStore();
        } catch (IOException e) {
            throw new IllegalStateException(ErrorMessage.FILE_LOAD_ERROR.getMessage(), e);
        }
    }

    private static ConvenienceStore createStore() throws IOException {
        String promotionsContent = Files.readString(Paths.get(PROMOTIONS_FILE));
        String productsContent = Files.readString(Paths.get(PRODUCTS_FILE));

        Map<String, Promotion> promotionMap = parsePromotions(promotionsContent);
        Map<String, Product> products = parseProducts(productsContent, promotionMap);

        return new ConvenienceStore(products);
    }


    private static Map<String, Promotion> parsePromotions(String content) {
        List<Promotion> promotions = FileParser.parsePromotions(content);
        return promotions.stream()
                .collect(Collectors.toMap(Promotion::getName, p -> p));
    }

    private static Map<String, Product> parseProducts(String content, Map<String, Promotion> promotionMap) {
        return FileParser.parseProducts(content, promotionMap);
    }


}
