package com.library.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);

    // Empty ANSI codes to keep terminal output simple and clean
    public static final String RESET = "";
    public static final String BOLD = "";
    public static final String BLACK = "";
    public static final String RED = "";
    public static final String GREEN = "";
    public static final String YELLOW = "";
    public static final String BLUE = "";
    public static final String PURPLE = "";
    public static final String CYAN = "";
    public static final String WHITE = "";

    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("[WARNING] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void printHeader(String title) {
        System.out.println("\n--- " + title.toUpperCase() + " ---");
    }

    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            printError("Input cannot be empty.");
        }
    }

    public static String readStringOptional(String prompt, String defaultValue) {
        System.out.print(prompt + " [" + defaultValue + "]: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                printError(String.format("Value must be between %d and %d.", min, max));
            } catch (NumberFormatException e) {
                printError("Invalid number format.");
            }
        }
    }

    public static int readIntOptional(String prompt, int defaultValue, int min, int max) {
        while (true) {
            System.out.print(prompt + " [" + defaultValue + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                printError(String.format("Value must be between %d and %d.", min, max));
            } catch (NumberFormatException e) {
                printError("Invalid number format.");
            }
        }
    }

    public static double readDouble(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min) {
                    return value;
                }
                printError(String.format("Value must be at least %.2f.", min));
            } catch (NumberFormatException e) {
                printError("Invalid decimal format.");
            }
        }
    }

    public static String readEmail(String prompt) {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        while (true) {
            String email = readString(prompt);
            if (emailPattern.matcher(email).matches()) {
                return email;
            }
            printError("Invalid email format.");
        }
    }

    public static String readEmailOptional(String prompt, String defaultValue) {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        while (true) {
            System.out.print(prompt + " [" + defaultValue + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            if (emailPattern.matcher(input).matches()) {
                return input;
            }
            printError("Invalid email format.");
        }
    }

    public static String readPhone(String prompt) {
        Pattern phonePattern = Pattern.compile("^\\+?[0-9\\-\\s()]{7,15}$");
        while (true) {
            String phone = readString(prompt);
            if (phonePattern.matcher(phone).matches()) {
                return phone;
            }
            printError("Invalid phone format (7-15 digits).");
        }
    }

    public static String readPhoneOptional(String prompt, String defaultValue) {
        Pattern phonePattern = Pattern.compile("^\\+?[0-9\\-\\s()]{7,15}$");
        while (true) {
            System.out.print(prompt + " [" + defaultValue + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return defaultValue;
            }
            if (phonePattern.matcher(input).matches()) {
                return input;
            }
            printError("Invalid phone format.");
        }
    }

    public static String readISBN(String prompt) {
        Pattern isbnPattern = Pattern.compile("^[0-9\\-X]{10,17}$");
        while (true) {
            String isbn = readString(prompt).toUpperCase().replace(" ", "");
            if (isbnPattern.matcher(isbn).matches()) {
                return isbn;
            }
            printError("Invalid ISBN (10 or 13 digits).");
        }
    }

    /**
     * Prints a clean, simple text-based table.
     */
    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) {
            return;
        }

        int columns = headers.length;
        int[] colWidths = new int[columns];

        for (int i = 0; i < columns; i++) {
            colWidths[i] = headers[i].length();
        }

        for (String[] row : rows) {
            for (int i = 0; i < columns; i++) {
                if (i < row.length && row[i] != null) {
                    colWidths[i] = Math.max(colWidths[i], row[i].length());
                }
            }
        }

        // Print header
        StringBuilder headerLine = new StringBuilder();
        for (int i = 0; i < columns; i++) {
            String format = "%-" + (colWidths[i] + 3) + "s";
            headerLine.append(String.format(format, headers[i]));
        }
        System.out.println(headerLine.toString());

        // Underline header
        int totalLength = headerLine.length();
        System.out.println("-".repeat(totalLength));

        // Print rows
        if (rows.isEmpty()) {
            System.out.println("No records found.");
        } else {
            for (String[] row : rows) {
                StringBuilder rowLine = new StringBuilder();
                for (int i = 0; i < columns; i++) {
                    String val = (i < row.length && row[i] != null) ? row[i] : "";
                    String format = "%-" + (colWidths[i] + 3) + "s";
                    rowLine.append(String.format(format, val));
                }
                System.out.println(rowLine.toString());
            }
        }
        System.out.println("-".repeat(totalLength));
    }
}
