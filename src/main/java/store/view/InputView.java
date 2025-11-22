package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.ArrayList;
import java.util.List;
import store.constant.ErrorMessage;
import store.domain.vo.OrderItem;

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
}
