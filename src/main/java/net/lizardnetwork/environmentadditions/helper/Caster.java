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
}
