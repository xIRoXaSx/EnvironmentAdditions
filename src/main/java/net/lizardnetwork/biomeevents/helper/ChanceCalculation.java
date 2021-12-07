package net.lizardnetwork.biomeevents.helper;

import java.util.concurrent.ThreadLocalRandom;

public class ChanceCalculation {
    private final Integer calculatedIndex;
    Integer start;
    Integer max;

    /**
     * A chance calculation between a <code>start</code> int and <code>max</code> int
     * @param start <code>Int</code> - The lowest possible value (inclusive)
     * @param max <code>Int</code> - The max bound (exclusive)
     */
    public ChanceCalculation(int start, int max) {
        this.start = start;
        this.max = max;

        if (start == max) {
            this.calculatedIndex = start;
            return;
        }

        // Change values if swapped
        if (start > max) {
            int tmp = start;
            start = max;
            max = tmp;
        }

        this.calculatedIndex = ThreadLocalRandom.current().nextInt(start, max);
    }

    /**
     * Check if the calculated index is the same as the given one
     * @return <code>Boolean</code> - <code>True</code> if both indexes are matching, <c>false</c> if they don't
     */
    public boolean matchedIndex() {
        return start.equals(calculatedIndex);
    }


    /**
     * Get the random Integer
     * @return <code>Integer</code> - The calculated random number
     */
    public Integer getRandom() {
        return start;
    }
}
