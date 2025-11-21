package store.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.domain.entity.Product;
import store.domain.vo.Promotion;
import store.domain.vo.PurchaseResult;

class MembershipCalculatorTest {

    private MembershipCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new MembershipCalculator();
    }

    @Test
    void 프로모션_미적용_금액만_할인() {
        // 콜라 3개 (2+1): 2000원 (1000원은 프로모션)
        // 에너지바 5개: 10000원 (프로모션 없음)
        // 프로모션 미적용 금액 = 10000원
        // 멤버십 할인 = 3000원

        Map<Product, PurchaseResult> purchases = new HashMap<>();

        Product cola = new Product("콜라", 1000, 10, 10,
                new Promotion("탄산2+1", 2, 1,
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));
        Product energyBar = new Product("에너지바", 2000, 0, 5);

        purchases.put(cola, new PurchaseResult.Builder("콜라", 3)
                .payQuantity(2)
                .freeQuantity(1)
                .build());

        purchases.put(energyBar, new PurchaseResult.Builder("에너지바", 5)
                .payQuantity(5)
                .freeQuantity(0)
                .build());

        int discount = calculator.calculateDiscount(purchases);

        assertThat(discount).isEqualTo(3000);
    }

    @Test
    void 멤버십_할인_최대_8000원() {
        // 30000원 구매 → 9000원 할인이지만 8000원만
        Map<Product, PurchaseResult> purchases = new HashMap<>();

        Product expensive = new Product("정식도시락", 6000, 0, 10);
        purchases.put(expensive, new PurchaseResult.Builder("정식도시락", 5)
                .payQuantity(5)
                .freeQuantity(0)
                .build());

        int discount = calculator.calculateDiscount(purchases);

        assertThat(discount).isEqualTo(8000);
    }

}
