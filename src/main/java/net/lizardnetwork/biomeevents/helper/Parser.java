package net.lizardnetwork.biomeevents.helper;

import net.lizardnetwork.biomeevents.Logging;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {
    /**
     * Gets a string in gradient colors
     * @param text String, The string to colorize
     * @param startHexCode String, The hex code to start the gradient from
     * @return String - The colorized text
     */
    public static String getGradientText(String text, String startHexCode) {
        StringBuilder returnValue = new StringBuilder();
        startHexCode = startHexCode.startsWith("#") && startHexCode.length() > 1 ? startHexCode.substring(1) : "000000";
        int hexInteger = Integer.parseInt(startHexCode, 16);

        for (Character character : text.toCharArray()) {
            returnValue.append(getColorizedText("{#" + Integer.toHexString(hexInteger) + "}" + character));
            hexInteger = hexInteger + 8;
        }

        // Reset color code at end of string
        returnValue.append(getColorizedText("&r"));
        return returnValue.toString();
    }

    /**
     * Gets a colorized string from either hex or alternate color codes
     * @param text String, The string containing either hex or alternate color codes
     * @return String - The colorized text
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
     * @return Integer - Either parsed from parameter 'value' or fallback
     */
    public static Integer parse(@Nullable String value, Integer fallbackValue) {
        var returnValue = fallbackValue;

        try {
            if (value != null && !value.isBlank() && !value.equals("null"))
                returnValue = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            Logging.warning(ex.getMessage());
        }

        return returnValue;
    }

    /**
     * Try to parse a float from string
     * Fallback value will be taken if parsing was unsuccessful
     * @param value The value to parse
     * @param fallbackValue The value to take as fallback if parameter 'value' cannot be parsed
     * @return Integer - Either parsed from parameter 'value' or fallback
     */
    public static Float parse(@Nullable String value, Float fallbackValue) {
        var returnValue = fallbackValue;

        try {
            if (value != null && !value.isBlank() && !value.equals("null"))
                returnValue = Float.parseFloat(value);
        }  catch (NumberFormatException ex) {
            Logging.warning("Error parsing Float... Using fallback value... Error: " + ex.getMessage());
        }

        return returnValue;
    }

    /**
     * Try to parse a boolean from string
     * Fallback value will be taken if parsing was unsuccessful
     * @param value The value to parse
     * @param fallbackValue The value to take as fallback if parameter 'value' cannot be parsed
     * @return Boolean - Either parsed from parameter 'value' or fallback
     */
    public static Boolean parse(@Nullable String value, Boolean fallbackValue) {
        var returnValue = fallbackValue;

        try {
            if (value != null && !value.isBlank() && !value.equals("null"))
                returnValue = Boolean.parseBoolean(value);
        } catch (NumberFormatException ex) {
            Logging.warning("Error parsing Boolean... Using fallback value... Error: " + ex.getMessage());
        }

        return returnValue;
    }

    /**
     * Try to parse a sound category from string
     * @param value The value to parse
     * @return SoundCategory - Either parsed from parameter 'value' or fallback
     */
    public static SoundCategory parse(@Nullable String value, SoundCategory soundCategory) {
        SoundCategory returnValue = soundCategory;

        try {
            if (value != null && !value.isBlank() && !value.equals("null"))
                returnValue = SoundCategory.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            Logging.warning("Error parsing SoundCategory: " + ex.getMessage());
        }

        return returnValue;
    }
}
