package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.Logging;
import org.bukkit.ChatColor;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static boolean isEmpty(Object value) {
        return value == null || value.toString().isBlank() || value.toString().equalsIgnoreCase("null");
    }

    /**
     * Get the Enum by String name.
     * If <code>IllegalArgumentException</code> gets caught, e will be cast to it's 0 value.
     * NOTE: Will also handle upper / lower cases!
     * @param e Class - The class of the target.
     * @param value String - The value to look for.
     * @return Enum - The target Enum or <code>null</code>.
     */
    public static <E extends Enum<E>>E valueOf(Class<E>e, Object value) {
        if (!e.isEnum()) {
            Logging.warn( e.getSimpleName() + " is not an enum!");
            return null;
        }

        if (isEmpty(value)) {
            return e.getEnumConstants()[0];
        }

        try {
            return Enum.valueOf(e, value.toString().toUpperCase());
        } catch (IllegalArgumentException ex) {
            Logging.warn("Invalid enum value " + value + " for type " + e.getSimpleName());
            return e.getEnumConstants()[0];
        }
    }

    public static int parse(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (IllegalArgumentException ex) {
            Logging.warn("Unable to parse " + value + " to an integer, fallback: " + fallback);
        }
        return fallback;
    }

    /**
     * Validates 3 and 6 digit hex color codes.
     * In case the value is a 3-digit code, the completed 6-digit code will be returned.
     * @param value String - The hex value to validate.
     * @return String - The validated hex color code or a fallback to ffffff if an error / invalid input occurs.
     */
    public static String validateHexColor(String value) {
        String fallback = "ffffff";
        if (isEmpty(value) || value.length() < 3) {
            Logging.warn("Invalid hex color string, fallback: #" + fallback + "!");
            return fallback;
        }
        value = value.replace("#", "");
        if (value.length() == 3) {
            // Convert 3 digit hex color to 6 digits (#RGB -> #RRGGBB).
            StringBuilder sb = new StringBuilder();
            for (Character c : value.toCharArray()) {
                sb.append(c).append(c);
            }
            return sb.toString();
        }

        Pattern pattern = Pattern.compile("[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            return value;
        }
        Logging.warn(value + " is not a valid hex color, fallback: #" + fallback + "!");
        return fallback;
    }

    /**
     * Parse the given value to Color.
     * @param value Object - The object to parse from.
     * @return org.bukkit.Color - The parsed color, fallback: white.
     */
    public static org.bukkit.Color hexToColor(Object value) {
        if (isEmpty(value)) {
            return org.bukkit.Color.fromRGB(255, 255, 255);
        }
        Color c = Color.decode("#" + validateHexColor(value.toString()));
        return org.bukkit.Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue());
    }

    /**
     * Add a gradient to the given text starting at startHex.
     * @param text String - The text to add the gradient to.
     * @param startHex String - The starting hex color.
     * @return String - The colorized text.
     */
    public static String gradientText(String text, String startHex) {
        StringBuilder returnValue = new StringBuilder();
        startHex = startHex.replace("#", "");
        startHex = startHex.length() > 1 ? startHex: "000000";
        int hexInteger = Integer.parseInt(startHex, 16);
        for (Character character : text.toCharArray()) {
            String hex = padHexColor(Integer.toHexString(hexInteger));
            returnValue.append(colorizeText("{#" + hex + "}" + character));
            hexInteger += 8;
        }
        returnValue.append(colorizeText("&r"));
        return returnValue.toString();
    }

    private static String padHexColor(String value) {
        int len = value.length();
        if (len < 6) {
            value = "0".repeat(6 - len) + value;
        }
        return value;
    }

    /**
     * Colorize the given value via alternate / hex color codes.
     * @param value String - The value to parse.
     * @return String - The colorized text.
     */
    public static String colorizeText(String value) {
        Pattern pattern = Pattern.compile("\\{(#[a-fA-F0-9]{3})}|\\{(#[a-fA-F0-9]{6})}");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String hex = padHexColor(value.substring(matcher.start() + 1, matcher.end() - 1));
            String hexColor = "#" + validateHexColor(hex);
            value = value.replace(matcher.group(0), net.md_5.bungee.api.ChatColor.of(hexColor).toString());
            matcher = pattern.matcher(value);
        }
        return ChatColor.translateAlternateColorCodes('&', value);
    }
}
