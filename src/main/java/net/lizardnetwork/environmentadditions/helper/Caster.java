package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.Logging;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Caster {
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
            Logging.warn("Unable to cast " + o.getClass().getSimpleName() + " to " + c.getSimpleName());
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
     * Cast o to a long value.
     * @param o Object - The object to cast.
     * @return long - The long value of o or 0.
     */
    public static long castToLong(Object o) {
        if (o instanceof Integer) {
            return (long)((int)o);
        }
        if (o instanceof Long) {
            return (long)o;
        }
        return 0;
    }
}