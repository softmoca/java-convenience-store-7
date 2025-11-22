package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import store.domain.vo.OrderItem;
import store.domain.vo.PurchaseResult;

public class InputView {

    public static List<OrderItem> readOrderItems() {
        return retryOnException(() -> {
            System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
            String input = Console.readLine();
            InputValidator.validateOrderInput(input);
            return parseOrderItems(input);
        });
    }

    private static List<OrderItem> parseOrderItems(String input) {
        List<OrderItem> items = new ArrayList<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            items.add(OrderItem.parse(part.trim()));
        }
        return items;
    }


    public static boolean readSuggestYesNo(PurchaseResult result) {
        String message = formatSuggestMessage(result);
        return readYesNo(message);
    }

    public static boolean readFullPriceYesNo(PurchaseResult result) {
        String message = formatFullPriceMessage(result);
        return readYesNo(message);
    }

    public static boolean readBuyYesNo() {
        String message = "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";
        return readYesNo(message);
    }

    public static boolean readMembershipYesNo() {
        String message = "멤버십 할인을 받으시겠습니까? (Y/N)";
        return readYesNo(message);
    }

    public static boolean readYesNo(String message) {
        return retryOnException(() -> {
            System.out.println(message);
            String input = Console.readLine().toUpperCase();
            InputValidator.validateYesNo(input);
            return input.equals("Y");
        });
    }


    private static String formatSuggestMessage(PurchaseResult result) {
        return String.format(
                "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)",
                result.getProductName(), result.getSuggestedAddition()
        );
    }

    private static String formatFullPriceMessage(PurchaseResult result) {
        return String.format(
                "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)",
                result.getProductName(), result.getFullPriceQuantity()
        );
    }

    private static <T> T retryOnException(Supplier<T> supplier) {
        while (true) {
            try {
                return supplier.get();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
