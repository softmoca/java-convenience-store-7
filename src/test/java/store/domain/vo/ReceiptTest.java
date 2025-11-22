package store.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import store.domain.aggregate.PurchaseContext;
import store.domain.entity.Product;

class ReceiptTest {

    @Test
    void 영수증_라인_아이템_생성() {
        // given
        PurchaseContext context = createSampleContext();

        // when
        Receipt receipt = new Receipt(context);

        // then
        assertThat(receipt.getPurchaseItems()).hasSize(2);
        assertThat(receipt.getFreeItems()).hasSize(1);

        Receipt.LineItem colaItem = receipt.getPurchaseItems().get(0);
        assertThat(colaItem.getName()).isEqualTo("콜라");
        assertThat(colaItem.getQuantity()).isEqualTo(3);
        assertThat(colaItem.getAmount()).isEqualTo(3000);
    }

    @Test
    void 증정품_없는_경우_빈_증정_목록() {
        // given
        PurchaseContext context = new PurchaseContext();
        Product water = new Product("물", 500, 0, 10);
        PurchaseResult result = new PurchaseResult.Builder("물", 3)
                .payQuantity(3)
                .freeQuantity(0)
                .build();
        context.addPurchase(water, result);

        // when
        Receipt receipt = new Receipt(context);

        // then
        assertThat(receipt.getFreeItems()).isEmpty();
    }

    @Test
    void 영수증_전체_금액_정보_통합() {
        // given
        PurchaseContext context = createContextWithDiscounts();

        // when
        Receipt receipt = new Receipt(context);

        // then
        assertThat(receipt.getTotalAmount()).isEqualTo(13000);
        assertThat(receipt.getPromotionDiscount()).isEqualTo(1000);
        assertThat(receipt.getMembershipDiscount()).isEqualTo(3000);
        assertThat(receipt.getFinalAmount()).isEqualTo(9000);
    }

    private PurchaseContext createSampleContext() {
        PurchaseContext context = new PurchaseContext();

        Promotion promotion = new Promotion("탄산2+1", 2, 1,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        Product cola = new Product("콜라", 1000, 10, 10, promotion);
        Product energyBar = new Product("에너지바", 2000, 0, 5);

        context.addPurchase(cola, new PurchaseResult.Builder("콜라", 3)
                .payQuantity(2).freeQuantity(1).build());
        context.addPurchase(energyBar, new PurchaseResult.Builder("에너지바", 2)
                .payQuantity(2).freeQuantity(0).build());

        return context;
    }

    private PurchaseContext createContextWithDiscounts() {
        PurchaseContext context = new PurchaseContext();

        Promotion promotion = new Promotion("탄산2+1", 2, 1,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        Product cola = new Product("콜라", 1000, 10, 10, promotion);
        Product energyBar = new Product("에너지바", 2000, 0, 5);

        context.addPurchase(cola, new PurchaseResult.Builder("콜라", 3)
                .payQuantity(2).freeQuantity(1).build());

        context.addPurchase(energyBar, new PurchaseResult.Builder("에너지바", 5)
                .payQuantity(5).freeQuantity(0).build());

        context.applyMembershipDiscount(true);
        return context;
    }
}
