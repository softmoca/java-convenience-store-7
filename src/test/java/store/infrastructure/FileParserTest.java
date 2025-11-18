package store.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import store.domain.Product;
import store.domain.Promotion;

class FileParserTest {

    @Test
    void 프로모션_파일_파싱() {
        String content = """
            name,buy,get,start_date,end_date
            탄산2+1,2,1,2025-01-01,2025-12-31
            MD추천상품,1,1,2025-01-01,2025-12-31
            """;

        FileParser parser = new FileParser();
        List<Promotion> promotions = parser.parsePromotions(content);

        assertThat(promotions).hasSize(2);
        assertThat(promotions.get(0).getName()).isEqualTo("탄산2+1");
        assertThat(promotions.get(0).getBuy()).isEqualTo(2);
    }

    @Test
    void 상품_파일_파싱() {
        String content = """
        name,price,quantity,promotion
        콜라,1000,10,탄산2+1
        콜라,1000,10,null
        사이다,1000,8,탄산2+1
        사이다,1000,7,null
        """;

        Map<String, Promotion> promotionMap = new HashMap<>();
        Promotion promo = new Promotion("탄산2+1", 2, 1,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        promotionMap.put("탄산2+1", promo);

        FileParser parser = new FileParser();
        Map<String, Product> products = parser.parseProducts(content, promotionMap);

        assertThat(products).containsKey("콜라");
        Product cola = products.get("콜라");
        assertThat(cola.getPromotionStock()).isEqualTo(10);
        assertThat(cola.getRegularStock()).isEqualTo(10);
    }




}
