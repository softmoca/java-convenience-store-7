package store.controller;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.config.StoreInitializer;
import store.domain.aggregate.ConvenienceStore;
import store.domain.aggregate.PurchaseContext;
import store.domain.entity.Product;
import store.domain.vo.OrderItem;
import store.domain.vo.PurchaseResult;
import store.domain.vo.Receipt;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private ConvenienceStore store;

    public void run() {
        store = StoreInitializer.initialize();

        do {
            OutputView.printWelcome();
            OutputView.printProducts(store.getProducts());

            processPurchase();

        } while (InputView.readYesNo(
                "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)"
        ));
    }

    private void processPurchase() {
        try {
            // 1. 구매 항목 입력
            List<OrderItem> items = InputView.readOrderItems();

            // 2. 구매 처리
            LocalDate today = DateTimes.now().toLocalDate();
            PurchaseContext context = store.processPurchase(items, today);

            // 3. 프로모션 질문 처리
            handlePromotionQuestions(context);

            // 4. 멤버십 할인
            boolean applyMembership = InputView.readYesNo(
                    "멤버십 할인을 받으시겠습니까? (Y/N)"
            );
            context.applyMembershipDiscount(applyMembership);

            // 5. 영수증 출력
            Receipt receipt = new Receipt(context);
            OutputView.printReceipt(receipt);

            // 6. 재고 차감
            store.updateStock(context);

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            processPurchase();  // 재시도
        }
    }

    private void handlePromotionQuestions(PurchaseContext context) {
        Map<Product, PurchaseResult> purchases = new HashMap<>(context.getPurchases());

        for (Map.Entry<Product, PurchaseResult> entry : purchases.entrySet()) {
            Product product = entry.getKey();
            PurchaseResult result = entry.getValue();

            //  추가 구매 제안
            if (result.shouldSuggestAddition()) {
                boolean accept = InputView.readSuggestYesNo(result);

                if (accept) {
                    PurchaseResult newResult = result.acceptAddition();
                    context.updatePurchase(product, newResult);
                }
            }

            //  정가 결제 확인
            else if (result.requiresFullPrice()) {
                boolean accept = InputView.readFullPricYesNo(result);

                if (!accept) {
                    // 정가 결제 거부 → 수량 감소
                    PurchaseResult newResult = result.rejectFullPrice();
                    context.updatePurchase(product, newResult);
                }
            }
        }
    }


}
