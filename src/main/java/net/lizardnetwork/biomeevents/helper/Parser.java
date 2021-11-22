package net.lizardnetwork.biomeevents.helper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {
    /**
     * Gets a colorized string from either hex or alternate color codes
     * @param text String, The string containing either hex or alternate color codes
     * @return String, The colorized text
     */
    public static String getColorizedText(String text) {
        String returnValue = ChatColor.translateAlternateColorCodes('&', text);
        Pattern pattern = Pattern.compile("\\{(#[a-fA-F0-9]{3})}|\\{(#[a-fA-F0-9]{6})}");
        Matcher matcher = pattern.matcher(returnValue);

        while (matcher.find()) {
            String hexColor = returnValue.substring(matcher.start() + 1, matcher.end() - 1);

            // Convert to 6 digits if only 3 are given (#RGB -> #RRGGBB)
            hexColor = hexColor.length() == 4 ? "#" + Arrays.stream(hexColor.replace("#", "")
                .split("")).map(x -> x + x).collect(Collectors.joining()) : hexColor;

            returnValue = returnValue.replace(matcher.group(0), net.md_5.bungee.api.ChatColor.of(hexColor) + "");
            matcher = pattern.matcher(returnValue);
        }

        return returnValue;
    }

    /**
     * Try to parse an integer from string
     * Fallback value will be taken if parsing was unsuccessful
     * @param value The value to parse
     * @param fallbackValue The value to take as fallback if parameter 'value' cannot be parsed
     * @return Integer, either parsed from parameter 'value' or fallback
     */
    public static Integer parse(String value, Integer fallbackValue) {
        var returnValue = fallbackValue;

        try {
            if (value != null && !value.isBlank())
                returnValue = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            Bukkit.getLogger().warning(ex.getMessage());
        }

        return returnValue;
    }

    /**
     * Try to parse a float from string
     * Fallback value will be taken if parsing was unsuccessful
     * @param value The value to parse
     * @param fallbackValue The value to take as fallback if parameter 'value' cannot be parsed
     * @return Integer, either parsed from parameter 'value' or fallback
     */
    public static Float parse(String value, Float fallbackValue) {
        var returnValue = fallbackValue;

        try {
            if (value != null && !value.isBlank())
                returnValue = Float.parseFloat(value);
        }  catch (NumberFormatException ex) {
            Bukkit.getLogger().warning("Error parsing Float... Using fallback value... Error: " + ex.getMessage());
        }

        return returnValue;
    }
}
