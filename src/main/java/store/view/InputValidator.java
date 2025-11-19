package store.view;

public class InputValidator {

    public static void validateOrderInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
            );
        }

        if (!input.contains("[") || !input.contains("]")) {
            throw new IllegalArgumentException(
                    "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
            );
        }

        String[] items = input.split(",");
        for (String item : items) {
            validateItemFormat(item.trim());
        }
    }

    private static void validateItemFormat(String item) {
        if (!item.startsWith("[") || !item.endsWith("]")) {
            throw new IllegalArgumentException(
                    "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
            );
        }

        String content = item.substring(1, item.length() - 1);
        String[] parts = content.split("-");

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
            );
        }

        try {
            int quantity = Integer.parseInt(parts[1]);
            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
                );
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
            );
        }
    }

    public static void validateYesNo(String input) {
        if (input == null ||
                (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N"))) {
            throw new IllegalArgumentException(
                    "[ERROR] 잘못된 입력입니다. 다시 입력해 주세요."
            );
        }
    }
}
