package store.controller;

import camp.nextstep.edu.missionutils.DateTimes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import store.domain.ConvenienceStore;
import store.domain.OrderItem;
import store.domain.Product;
import store.domain.Promotion;
import store.domain.PurchaseContext;
import store.domain.PurchaseResult;
import store.domain.Receipt;
import store.infrastructure.FileParser;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {
    private final InputView inputView;
    private final OutputView outputView;
    private ConvenienceStore store;

    public StoreController() {
        this.inputView = new InputView();
        this.outputView = new OutputView();
    }

    public void run() {
        try {
            initializeStore();

            do {
                outputView.printWelcome();
                outputView.printProducts(store.getProducts());

                processPurchase();

            } while (inputView.readYesNo(
                    "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)"
            ));

        } catch (Exception e) {
            System.out.println("[ERROR] 시스템 오류가 발생했습니다.");
        }
    }


    private void initializeStore() {
        try {
            // 파일 읽기
            String promotionsContent = Files.readString(
                    Paths.get("src/main/resources/promotions.md")
            );
            String productsContent = Files.readString(
                    Paths.get("src/main/resources/products.md")
            );

            FileParser parser = new FileParser();
            List<Promotion> promotions = parser.parsePromotions(promotionsContent);
            Map<String, Promotion> promotionMap = promotions.stream()
                    .collect(Collectors.toMap(Promotion::getName, p -> p));

            Map<String, Product> products = parser.parseProducts(productsContent, promotionMap);

            store = new ConvenienceStore(products);

        } catch (IOException e) {
            throw new IllegalStateException("파일을 읽을 수 없습니다.");
        }
    }

    private void processPurchase() {
        try {
            // 1. 구매 항목 입력
            List<OrderItem> items = inputView.readOrderItems();

            // 2. 구매 처리
            LocalDate today = DateTimes.now().toLocalDate();
            PurchaseContext context = store.processPurchase(items, today);

            // 3. 프로모션 질문 처리
            handlePromotionQuestions(context);

            // 4. 멤버십 할인
            boolean applyMembership = inputView.readYesNo(
                    "멤버십 할인을 받으시겠습니까? (Y/N)"
            );
            context.applyMembershipDiscount(applyMembership);

            // 5. 영수증 출력
            Receipt receipt = new Receipt(context);
            outputView.printReceipt(receipt);

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
                boolean accept = inputView.readYesNo(result.getSuggestedMessage());

                if (accept) {
                    // 추가 구매 수락 → 수량 증가
                    PurchaseResult newResult = result.acceptAddition();
                    context.updatePurchase(product, newResult);
                }
            }

            //  정가 결제 확인
            else if (result.requiresFullPrice()) {
                boolean accept = inputView.readYesNo(result.getFullPriceMessage());

                if (!accept) {
                    // 정가 결제 거부 → 수량 감소
                    PurchaseResult newResult = result.rejectFullPrice();
                    context.updatePurchase(product, newResult);
                }
            }
        }
    }


}
