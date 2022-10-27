package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.Logging;

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
    public static <E extends Enum<E>>E valueOf(Class<E>e, String value) {
        if (!e.isEnum()) {
            Logging.warn( e.getSimpleName() + " is not an enum!");
            return null;
        }

        if (isEmpty(value)) {
            return e.getEnumConstants()[0];
        }

        try {
            return Enum.valueOf(e, value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            Logging.warn("Invalid enum value " + value + " for type " + e.getSimpleName());
            return e.getEnumConstants()[0];
        }
    }
}
