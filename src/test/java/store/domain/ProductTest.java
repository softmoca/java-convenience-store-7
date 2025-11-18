package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void 상품_생성_및_재고_확인() {
        // 콜라: 프로모션 재고 10개, 일반 재고 10개
        Product product = new Product("콜라", 1000, 10, 10);

        assertThat(product.getName()).isEqualTo("콜라");
        assertThat(product.getPrice()).isEqualTo(1000);
        assertThat(product.getTotalStock()).isEqualTo(20);
    }

    @Test
    void 재고보다_많이_구매할_수_없다() {
        Product product = new Product("콜라", 1000, 10, 10);

        assertThat(product.canPurchase(20)).isTrue();
        assertThat(product.canPurchase(21)).isFalse();
    }

    @Test
    void 프로모션이_있는_상품_생성() {
        Promotion promotion = new Promotion(
                "탄산2+1", 2, 1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        );

        Product product = new Product("콜라", 1000, 10, 10, promotion);

        assertThat(product.hasPromotion()).isTrue();
        assertThat(product.getPromotion()).isEqualTo(promotion);
    }


}
