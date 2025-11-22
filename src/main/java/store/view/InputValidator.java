package store.view;

import store.constant.ErrorMessage;

public class InputValidator {

    public static void validateOrderInput(String input) {
        validateNotEmpty(input);
        validateContainsBrackets(input);
        validateEachItem(input);
    }

    private static void validateNotEmpty(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    private static void validateContainsBrackets(String input) {
        if (!input.contains("[") || !input.contains("]")) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    private static void validateEachItem(String input) {
        String[] items = input.split(",");
        for (String item : items) {
            validateSingleItem(item.trim());
        }
    }

    private static void validateSingleItem(String item) {
        validateBracketFormat(item);
        String content = extractContent(item);
        validateItemContent(content);
    }

    private static void validateBracketFormat(String item) {
        if (!item.startsWith("[") || !item.endsWith("]")) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    private static String extractContent(String item) {
        return item.substring(1, item.length() - 1);
    }

    private static void validateItemContent(String content) {
        String[] parts = content.split("-");
        validatePartCount(parts);
        validateQuantity(parts[1]);
    }

    private static void validatePartCount(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    private static void validateQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);
            validatePositiveNumber(quantity);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    private static void validatePositiveNumber(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    public static void validateYesNo(String input) {
        if (input == null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_INPUT.getMessage());
        }

        if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_INPUT.getMessage());
        }
    }
}
