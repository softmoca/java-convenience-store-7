package store.domain.vo;


import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import store.domain.aggregate.PurchaseContext;
import store.domain.entity.Product;

class ReceiptTest {

    @Test
    void 영수증_생성_및_계산() {
        PurchaseContext context = new PurchaseContext();

        Product cola = new Product("콜라", 1000, 10, 10,
                new Promotion("탄산2+1", 2, 1,
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)));

        PurchaseResult colaResult = new PurchaseResult.Builder("콜라", 3)
                .payQuantity(2)
                .freeQuantity(1)
                .build();

        // 에너지바 5개 (프로모션 없음)
        Product energyBar = new Product("에너지바", 2000, 0, 5);
        PurchaseResult energyResult = new PurchaseResult.Builder("에너지바", 5)
                .payQuantity(5)
                .freeQuantity(0)
                .build();

        context.addPurchase(cola, colaResult);
        context.addPurchase(energyBar, energyResult);
        context.applyMembershipDiscount(true);

        Receipt receipt = new Receipt(context);

        assertSoftly(softly -> {
            // 구매 항목 검증
            Receipt.LineItem colaItem = findItem(receipt, "콜라");
            Receipt.LineItem energyBarItem = findItem(receipt, "에너지바");

            softly.assertThat(receipt.getPurchaseItems()).hasSize(2);
            softly.assertThat(colaItem.getQuantity()).isEqualTo(3);
            softly.assertThat(colaItem.getAmount()).isEqualTo(3000);
            softly.assertThat(energyBarItem.getQuantity()).isEqualTo(5);
            softly.assertThat(energyBarItem.getAmount()).isEqualTo(10000);

            // 증정 항목 검증
            softly.assertThat(receipt.getFreeItems()).hasSize(1);
            softly.assertThat(receipt.getFreeItems().get(0).getName()).isEqualTo("콜라");
            softly.assertThat(receipt.getFreeItems().get(0).getQuantity()).isEqualTo(1);

            // 금액 검증
            softly.assertThat(receipt.getTotalQuantity()).isEqualTo(8);
            softly.assertThat(receipt.getTotalAmount()).isEqualTo(13000);
            softly.assertThat(receipt.getPromotionDiscount()).isEqualTo(1000);
            softly.assertThat(receipt.getMembershipDiscount()).isEqualTo(3000);
            softly.assertThat(receipt.getFinalAmount()).isEqualTo(9000);
        });
    }

    private Receipt.LineItem findItem(Receipt receipt, String name) {
        return receipt.getPurchaseItems().stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

}
