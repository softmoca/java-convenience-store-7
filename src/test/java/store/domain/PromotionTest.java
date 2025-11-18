package store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PromotionTest {

    @Test
    void 프로모션_기간_내_날짜는_적용된다() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        Promotion promotion = new Promotion(
                "탄산2+1", 2, 1, startDate, endDate
        );

        LocalDate testDate = LocalDate.of(2025, 6, 15);

        assertThat(promotion.isAvailable(testDate)).isTrue();
    }
}
