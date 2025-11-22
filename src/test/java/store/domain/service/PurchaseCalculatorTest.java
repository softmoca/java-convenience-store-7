package store.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.domain.entity.Product;
import store.domain.vo.Promotion;
import store.domain.vo.PurchaseResult;

class PurchaseCalculatorTest {

    private Product product;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.of(2025, 11, 15);

        Promotion promotion = new Promotion(
                "탄산2+1", 2, 1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        product = new Product("콜라", 1000, 10, 10, promotion);
    }

    @Test
    void 정상_프로모션_적용() {
        // 3개 구매, 2개 결제, 1개 무료
        PurchaseResult result = PurchaseCalculator.calculate(product, 3, today);

        assertThat(result.getPayQuantity()).isEqualTo(2);
        assertThat(result.getFreeQuantity()).isEqualTo(1);
        assertThat(result.getTotalQuantity()).isEqualTo(3);
    }

    @Test
    void 프로모션_수량_미달_시_추가구매_제안() {
        // 2개만 구매 → 1개 더 추가 제안
        PurchaseResult result = PurchaseCalculator.calculate(product, 2, today);

        assertThat(result.shouldSuggestAddition()).isTrue();
        assertThat(result.getSuggestedAddition()).isEqualTo(1);
    }

    @Test
    void 투플러스원_2개_구매시_1개_추가_제안() {
        PurchaseResult result = PurchaseCalculator.calculate(product, 2, today);

        assertThat(result.shouldSuggestAddition()).isTrue();
        assertThat(result.getSuggestedAddition()).isEqualTo(1);
    }

    @Test
    void 원플러스원_1개_구매시_1개_추가_제안() {
        Promotion oneP1usOne = new Promotion(
                "MD추천상품", 1, 1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );
        Product product = new Product("오렌지주스", 1800, 9, 0, oneP1usOne);

        PurchaseResult result = PurchaseCalculator.calculate(product, 1, today);

        assertThat(result.shouldSuggestAddition()).isTrue();
        assertThat(result.getSuggestedAddition()).isEqualTo(1);
    }

    @Test
    void 프로모션_재고_부족시_정가_결제_확인() {
        // 프로모션 재고 7개, 10개 구매 요청
        Product limitedCola = new Product("콜라", 1000, 7, 10,
                new Promotion("탄산2+1", 2, 1,
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))
        );

        PurchaseResult result = PurchaseCalculator.calculate(limitedCola, 10, today);

        // 6개는 프로모션 (4개 결제 + 2개 무료)
        // 4개는 정가
        assertThat(result.requiresFullPrice()).isTrue();
        assertThat(result.getFullPriceQuantity()).isEqualTo(4);
        assertThat(result.getPayQuantity()).isEqualTo(8);  // 4 + 4
        assertThat(result.getFreeQuantity()).isEqualTo(2);


    }


}
