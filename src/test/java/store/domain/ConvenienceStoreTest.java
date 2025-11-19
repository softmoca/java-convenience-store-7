package store.domain;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConvenienceStoreTest {

    private ConvenienceStore store;

    @BeforeEach
    void setUp() {
        Map<String, Product> products = new HashMap<>();

        Promotion promotion = new Promotion("탄산2+1", 2, 1,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

        products.put("콜라", new Product("콜라", 1000, 10, 10, promotion));
        products.put("물", new Product("물", 500, 0, 10));

        store = new ConvenienceStore(products);
    }

    @Test
    void 정상_구매_처리() {
        List<OrderItem> items = List.of(
                new OrderItem("콜라", 3),
                new OrderItem("물", 2)
        );

        LocalDate today = LocalDate.of(2025, 11, 15);
        PurchaseContext context = store.processPurchase(items, today);

        assertThat(context.getTotalPayAmount()).isEqualTo(3000);
        assertThat(context.getPromotionDiscount()).isEqualTo(1000);
    }
}
