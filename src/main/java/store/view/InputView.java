package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.ArrayList;
import java.util.List;
import store.constant.ErrorMessage;
import store.domain.vo.OrderItem;
import store.domain.vo.PurchaseResult;

public class InputView {

    public static List<OrderItem> readOrderItems() {
        while (true) {
            try {
                return tryReadOrderItems();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static List<OrderItem> tryReadOrderItems() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String input = Console.readLine();

        try {
            InputValidator.validateOrderInput(input);
            return parseOrderItems(input);
        } catch (Exception e) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    private static List<OrderItem> parseOrderItems(String input) {
        List<OrderItem> items = new ArrayList<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            items.add(OrderItem.parse(part.trim()));
        }
        return items;
    }


    public static boolean readYesNo(String message) {
        System.out.println(message);
        String input = Console.readLine().toUpperCase();
        InputValidator.validateYesNo(input);
        return input.equals("Y");
    }


    public static boolean readSuggestYesNo(PurchaseResult result) {
        while (true) {
            try {
                return tryReadSuggestYesNo(result);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public static boolean tryReadSuggestYesNo(PurchaseResult result) {
        System.out.println(String.format(
                "현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)",
                result.getProductName(), result.getSuggestedAddition()
        ));
        String input = Console.readLine().toUpperCase();
        InputValidator.validateYesNo(input);
        return input.equals("Y");
    }


    public static boolean readFullPricYesNo(PurchaseResult result) {
        while (true) {
            try {
                return tryReadFullPricYesNo(result);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public static boolean tryReadFullPricYesNo(PurchaseResult result) {
        System.out.println(String.format(
                "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)",
                result.getProductName(), result.getFullPriceQuantity()
        ));
        String input = Console.readLine().toUpperCase();
        InputValidator.validateYesNo(input);
        return input.equals("Y");
    }


}
