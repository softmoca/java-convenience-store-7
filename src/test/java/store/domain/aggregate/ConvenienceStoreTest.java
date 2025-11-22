package store.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.domain.entity.Product;
import store.domain.vo.OrderItem;
import store.domain.vo.Promotion;
import store.domain.vo.PurchaseResult;

class ConvenienceStoreTest {

    private ConvenienceStore store;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.of(2025, 11, 15);

        Map<String, Product> products = new HashMap<>();

        // 탄산2+1 프로모션
        Promotion carbonPromotion = new Promotion("탄산2+1", 2, 1,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

        // MD추천상품 1+1 프로모션
        Promotion mdPromotion = new Promotion("MD추천상품", 1, 1,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

        products.put("콜라", new Product("콜라", 1000, 10, 10, carbonPromotion));
        products.put("사이다", new Product("사이다", 1000, 8, 7, carbonPromotion));
        products.put("오렌지주스", new Product("오렌지주스", 1800, 9, 0, mdPromotion));
        products.put("물", new Product("물", 500, 0, 10));
        products.put("에너지바", new Product("에너지바", 2000, 0, 5));

        store = new ConvenienceStore(products);
    }

    @Test
    void 정상_구매_처리() {
        // given
        List<OrderItem> items = List.of(
                new OrderItem("콜라", 3),
                new OrderItem("물", 2)
        );

        // when
        PurchaseContext context = store.processPurchase(items, today);

        // then
        assertThat(context.calculateTotalAmount()).isEqualTo(3000); // 콜라 2000 + 물 1000
        assertThat(context.calculatePromotionDiscount()).isEqualTo(1000); // 콜라 1개 무료
    }

    @Test
    void 구매_후_재고_차감() {
        // given
        List<OrderItem> items = List.of(new OrderItem("콜라", 3));
        PurchaseContext context = store.processPurchase(items, today);

        // when
        store.updateStock(context);

        // then
        Map<String, Product> products = store.getProducts();
        Product cola = products.get("콜라");
        assertThat(cola.getTotalStock()).isEqualTo(17); // 20 - 3
    }

    @Test
    void 존재하지_않는_상품_구매시_예외() {
        // given
        List<OrderItem> items = List.of(new OrderItem("없는상품", 1));

        // when & then
        assertThatThrownBy(() -> store.processPurchase(items, today))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] 존재하지 않는 상품입니다");
    }

    @Test
    void 재고_초과_구매시_예외() {
        // given
        List<OrderItem> items = List.of(new OrderItem("콜라", 100));

        // when & then
        assertThatThrownBy(() -> store.processPurchase(items, today))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다");
    }

    @Test
    void 여러_상품_동시_구매() {
        // given
        List<OrderItem> items = List.of(
                new OrderItem("콜라", 6),      // 2+1 프로모션
                new OrderItem("오렌지주스", 2), // 1+1 프로모션
                new OrderItem("물", 3),         // 프로모션 없음
                new OrderItem("에너지바", 2)    // 프로모션 없음
        );

        // when
        PurchaseContext context = store.processPurchase(items, today);

        // then
        Map<Product, PurchaseResult> purchases = context.getPurchases();
        assertThat(purchases).hasSize(4);

        // 각 상품별 검증
        assertThat(context.calculateTotalAmount()).isEqualTo(11300); // 4000 + 1800 + 1500 + 4000
        assertThat(context.calculatePromotionDiscount()).isEqualTo(3800); // 콜라 2000 + 오렌지주스 1800
    }

    @Test
    void 프로모션_재고_부족시_일반_재고_사용() {
        // given
        List<OrderItem> items = List.of(new OrderItem("콜라", 15)); // 프로모션 재고 10, 일반 재고 10

        // when
        PurchaseContext context = store.processPurchase(items, today);

        // then
        Map<Product, PurchaseResult> purchases = context.getPurchases();
        PurchaseResult colaResult = purchases.values().iterator().next();

        // 9개는 프로모션(6개 결제 + 3개 무료), 6개는 정가
        assertThat(colaResult.getFreeQuantity()).isEqualTo(3);
        assertThat(colaResult.requiresFullPrice()).isTrue();
        assertThat(colaResult.getFullPriceQuantity()).isEqualTo(6);
    }

    @Test
    void 재고_완전_소진_후_재구매_불가() {
        // given
        List<OrderItem> firstPurchase = List.of(new OrderItem("에너지바", 5));
        PurchaseContext firstContext = store.processPurchase(firstPurchase, today);
        store.updateStock(firstContext);

        // when & then
        List<OrderItem> secondPurchase = List.of(new OrderItem("에너지바", 1));
        assertThatThrownBy(() -> store.processPurchase(secondPurchase, today))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR] 재고 수량을 초과");
    }


}
