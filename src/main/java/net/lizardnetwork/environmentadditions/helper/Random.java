package net.lizardnetwork.environmentadditions.helper;

import java.util.concurrent.ThreadLocalRandom;

public class Random {
    private final float result;

    /**
     * Generate a new PRN float from min (inclusive) to max (exclusive).
     * @param min int - The minimum.
     * @param max int - The maximum.
     */
    public Random(float min, float max) {
        min = Math.min(max, min);
        max = Math.max(max, min);
        max = (min == max) && (max < 0) ? -max : max;
        this.result = ThreadLocalRandom.current().nextFloat(min, max);
    }

    /**
     * Generate a new PRN int from min (inclusive) to max (exclusive).
     * @param min int - The minimum.
     * @param max int - The maximum.
     */
    public Random(int min, int max) {
        min = Math.min(max, min);
        max = Math.max(max, min);
        this.result = ThreadLocalRandom.current().nextInt(min, max);
    }

    public float getFloatResult() {
        return result;
    }

    public int getIntResult() {
        return (int) result;
    }
}
