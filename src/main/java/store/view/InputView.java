package store.view;

import camp.nextstep.edu.missionutils.Console;
import java.util.ArrayList;
import java.util.List;
import store.domain.vo.OrderItem;

public class InputView {

    public List<OrderItem> readOrderItems() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String input = Console.readLine();

        try {
            InputValidator.validateOrderInput(input);
            return parseOrderItems(input);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
            );
        }
    }

    private List<OrderItem> parseOrderItems(String input) {
        List<OrderItem> items = new ArrayList<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            items.add(OrderItem.parse(part.trim()));
        }
        return items;
    }

    public boolean readYesNo(String message) {
        System.out.println(message);
        String input = Console.readLine().toUpperCase();
        InputValidator.validateYesNo(input);
        return input.equals("Y");
    }
}
