package store.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import store.domain.Promotion;

class FileParserTest {

    @Test
    void 프로모션_파일_파싱() {
        String content = """
            name,buy,get,start_date,end_date
            탄산2+1,2,1,2024-01-01,2024-12-31
            MD추천상품,1,1,2024-01-01,2024-12-31
            """;

        FileParser parser = new FileParser();
        List<Promotion> promotions = parser.parsePromotions(content);

        assertThat(promotions).hasSize(2);
        assertThat(promotions.get(0).getName()).isEqualTo("탄산2+1");
        assertThat(promotions.get(0).getBuy()).isEqualTo(2);
    }
}
