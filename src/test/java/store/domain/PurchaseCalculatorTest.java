package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseCalculatorTest {

    private PurchaseCalculator calculator;
    private Product product;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        calculator = new PurchaseCalculator();
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
        PurchaseResult result = calculator.calculate(product, 3, today);

        assertThat(result.getPayQuantity()).isEqualTo(2);
        assertThat(result.getFreeQuantity()).isEqualTo(1);
        assertThat(result.getTotalQuantity()).isEqualTo(3);
    }

    @Test
    void 프로모션_수량_미달_시_추가구매_제안() {
        // 2개만 구매 → 1개 더 추가 제안
        PurchaseResult result = calculator.calculate(product, 2, today);

        assertThat(result.shouldSuggestAddition()).isTrue();
        assertThat(result.getSuggestedAddition()).isEqualTo(1);
    }

    @Test
    void 투플러스원_2개_구매시_1개_추가_제안() {
        PurchaseResult result = calculator.calculate(product, 2, today);

        assertThat(result.shouldSuggestAddition()).isTrue();
        assertThat(result.getSuggestedAddition()).isEqualTo(1);
        assertThat(result.getSuggestedMessage()).isEqualTo(
                "현재 콜라은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)"
        );
    }

    @Test
    void 원플러스원_1개_구매시_1개_추가_제안() {
        Promotion oneP1usOne = new Promotion(
                "MD추천상품", 1, 1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );
        Product product = new Product("오렌지주스", 1800, 9, 0, oneP1usOne);

        PurchaseResult result = calculator.calculate(product, 1, today);

        assertThat(result.shouldSuggestAddition()).isTrue();
        assertThat(result.getSuggestedAddition()).isEqualTo(1);
    }




}
