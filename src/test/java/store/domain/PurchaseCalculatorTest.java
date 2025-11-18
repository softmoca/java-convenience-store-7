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
        today = LocalDate.of(2024, 11, 15);

        Promotion promotion = new Promotion(
                "탄산2+1", 2, 1,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31)
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
}
