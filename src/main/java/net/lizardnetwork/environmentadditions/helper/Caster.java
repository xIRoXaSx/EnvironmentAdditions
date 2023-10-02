package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.Logging;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Caster {
    public static boolean castToBoolean(Object o, boolean fallback) {
        if (!(o instanceof Boolean)) {
            return fallback;
        }
        return (boolean)o;
    }

    public static Map<?,?> castToMap(Object o) {
        if (!(o instanceof Map<?, ?> map)) {
            Logging.warn("Unable to cast object to map");
            return null;
        }
        return map;
    }

    public static <T> List<T> castToList(Class<T> c, Object o) {
        List<T> values = new ArrayList<>();
        if (!(o instanceof List<?> casted)) {
            Logging.warn("Unable to cast " + c.toString() + " to " + c.toString());
            return values;
        }

        for (var elem : casted) {
            values.add(c.cast(elem));
        }
        return values;
    }

    /**
     * Check if o is an instance of String and return its value.
     * @param o Object - The object to use.
     * @return String - Either the object's String value if instance of String or empty String.
     */
    public static String valueOrEmpty(Object o) {
        if (!(o instanceof String value)) {
            return "";
        }
        return Parser.isEmpty(value) ? "" : value;
    }

    /**
     * Cast o to a float value.
     * @param o Object - The object to cast.
     * @return float - The float value of o or 0.
     */
    public static float castToFloat(Object o) {
        if (o instanceof Integer) {
            return (float)((int)o);
        }
        if (o instanceof Float) {
            return (float)o;
        }
        if (o instanceof Double) {
            return (float)((double)o);
        }
        return 0;
    }

    /**
     * Cast o to a double value.
     * @param o Object - The object to cast.
     * @return double - The double value of o or fallback.
     */
    public static double castToDouble(Object o, double fallback) {
        if (o instanceof Integer) {
            return (double)((int)o);
        }
        if (o instanceof Long) {
            return (double)((long)o);
        }
        if (o instanceof Double) {
            return (double)o;
        }
        return fallback;
    }

    /**
     * Cast o to a int value.
     * @param o Object - The object to cast.
     * @return int - The int value of o or fallback.
     */
    public static int castToInt(Object o, int fallback) {
        if (o instanceof Integer) {
            return ((int)o);
        }
        if (o instanceof Long) {
            return (int)((long)o);
        }
        if (o instanceof Double) {
            return (int)((double)o);
        }
        return fallback;
    }
}
