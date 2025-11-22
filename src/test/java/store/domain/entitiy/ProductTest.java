package store.domain.entitiy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import store.domain.entity.Product;

class ProductTest {


    @Test
    void 재고보다_많이_구매할_수_없다() {
        // given
        Product product = new Product("콜라", 1000, 10, 10);

        // when&then
        assertThat(product.canPurchase(20)).isTrue();
        assertThat(product.canPurchase(21)).isFalse();
    }

    @Test
    void 프로모션_재고_우선_차감() {
        // given
        Product product = new Product("콜라", 1000, 10, 10);

        // when
        product.deductStock(5);

        // then
        assertThat(product.getTotalStock()).isEqualTo(15);
    }

    @Test
    void 프로모션_재고_부족시_일반_재고에서_차감() {
        // given
        Product product = new Product("콜라", 1000, 3, 10);

        // when
        product.deductStock(7);

        // then
        assertThat(product.getTotalStock()).isEqualTo(6);
    }


}
