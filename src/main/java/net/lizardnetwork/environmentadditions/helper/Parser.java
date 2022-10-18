package net.lizardnetwork.environmentadditions.helper;

public class Parser {
    public static boolean isEmpty(String value) {
        return value == null || value.isBlank() || value.equalsIgnoreCase("null");
    }
}
