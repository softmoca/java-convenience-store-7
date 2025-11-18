package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

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
}
