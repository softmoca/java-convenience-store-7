package store.domain;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

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

        // 구매 항목
        assertThat(receipt.getPurchaseItems()).hasSize(2);
        assertThat(receipt.getPurchaseItems().get(1).getName()).isEqualTo("콜라");
        assertThat(receipt.getPurchaseItems().get(1).getQuantity()).isEqualTo(3);
        assertThat(receipt.getPurchaseItems().get(1).getAmount()).isEqualTo(3000);

        // 증정 항목
        assertThat(receipt.getFreeItems()).hasSize(1);
        assertThat(receipt.getFreeItems().get(0).getName()).isEqualTo("콜라");
        assertThat(receipt.getFreeItems().get(0).getQuantity()).isEqualTo(1);

        // 금액
        assertThat(receipt.getTotalQuantity()).isEqualTo(8);
        assertThat(receipt.getTotalAmount()).isEqualTo(13000);
        assertThat(receipt.getPromotionDiscount()).isEqualTo(1000);
        assertThat(receipt.getMembershipDiscount()).isEqualTo(3000);
        assertThat(receipt.getFinalAmount()).isEqualTo(9000);
    }
}
