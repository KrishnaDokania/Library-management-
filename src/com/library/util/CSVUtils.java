package com.library.util;

import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

    public static String toCSVLine(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            String field = fields[i];
            if (field == null) {
                field = "";
            }
            // Escape double quotes and check if it needs encapsulation
            boolean needsQuotes = field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r");
            String escaped = field.replace("\"", "\"\"");
            if (needsQuotes) {
                sb.append("\"").append(escaped).append("\"");
            } else {
                sb.append(escaped);
            }
        }
        return sb.toString();
    }

    public static String[] parseCSVLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return new String[0];
        }
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        int len = line.length();

        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes) {
                    // Check if it's an escaped double quote ("")
                    if (i + 1 < len && line.charAt(i + 1) == '"') {
                        currentField.append('"');
                        i++; // skip next quote
                    } else {
                        inQuotes = false;
                    }
                } else {
                    inQuotes = true;
                }
            } else if (c == ',') {
                if (inQuotes) {
                    currentField.append(c);
                } else {
                    fields.add(currentField.toString());
                    currentField.setLength(0);
                }
            } else {
                currentField.append(c);
            }
        }
        // Add the last field
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }
}
