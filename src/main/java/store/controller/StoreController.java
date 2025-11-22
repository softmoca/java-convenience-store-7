package store.controller;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
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
        runShoppingLoop();
    }

    private void runShoppingLoop() {
        do {
            displayWelcomeAndProducts();
            processPurchase();
        } while (InputView.readBuyYesNo());
    }

    private void displayWelcomeAndProducts() {
        OutputView.printWelcome();
        OutputView.printProducts(store.getProducts());
    }

    private void processPurchase() {
        List<OrderItem> items = InputView.readOrderItems();
        PurchaseContext context = createPurchaseContext(items);

        handlePromotionQuestions(context);
        applyMembershipIfNeeded(context);

        completeTransaction(context);
    }

    private PurchaseContext createPurchaseContext(List<OrderItem> items) {
        LocalDate today = DateTimes.now().toLocalDate();
        return store.processPurchase(items, today);
    }

    private void handlePromotionQuestions(PurchaseContext context) {
        Map<Product, PurchaseResult> purchases = context.getPurchases();

        for (Map.Entry<Product, PurchaseResult> entry : purchases.entrySet()) {
            handleSinglePromotion(context, entry.getKey(), entry.getValue());
        }
    }

    private void handleSinglePromotion(PurchaseContext context, Product product, PurchaseResult result) {
        handleAdditionSuggestion(context, product, result);
        handleFullPriceConfirmation(context, product, result);
    }

    private void handleAdditionSuggestion(PurchaseContext context, Product product, PurchaseResult result) {
        if (!result.shouldSuggestAddition()) {
            return;
        }

        if (!InputView.readSuggestYesNo(result)) {
            return;
        }

        updateWithAddition(context, product, result);
    }

    private void updateWithAddition(PurchaseContext context, Product product, PurchaseResult result) {
        PurchaseResult newResult = result.acceptAddition();
        context.updatePurchase(product, newResult);
    }

    private void handleFullPriceConfirmation(PurchaseContext context, Product product, PurchaseResult result) {
        if (!result.requiresFullPrice()) {
            return;
        }

        if (InputView.readFullPriceYesNo(result)) {
            return;
        }

        updateWithRejection(context, product, result);
    }

    private void updateWithRejection(PurchaseContext context, Product product, PurchaseResult result) {
        PurchaseResult newResult = result.rejectFullPrice();
        context.updatePurchase(product, newResult);
    }

    private void applyMembershipIfNeeded(PurchaseContext context) {
        boolean applyMembership = InputView.readMembershipYesNo();
        context.applyMembershipDiscount(applyMembership);
    }

    private void completeTransaction(PurchaseContext context) {
        Receipt receipt = new Receipt(context);
        OutputView.printReceipt(receipt);
        store.updateStock(context);
    }
}
