package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.Logging;
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

    public static org.bukkit.Color hexToColor(Object value, boolean validate) {
        if (isEmpty(value)) {
            return org.bukkit.Color.fromRGB(255, 255, 255);
        }
        Color c = validate ? Color.decode("#" + validateHexColor(value.toString())) : Color.decode(value.toString());
        return org.bukkit.Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue());
    }
}
