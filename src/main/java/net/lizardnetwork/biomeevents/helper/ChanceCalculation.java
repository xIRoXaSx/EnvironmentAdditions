package net.lizardnetwork.biomeevents.helper;

import java.util.concurrent.ThreadLocalRandom;

public class ChanceCalculation {
    private final Integer calculatedIndex;
    Integer randomIndex;
    Integer max;

    /**
     * A chance calculation between a <code>start</code> int and <code>max</code> int
     * @param start <code>Int</code> - The lowest possible value (inclusive)
     * @param max <code>Int</code> - The max bound (exclusive)
     */
    public ChanceCalculation(int start, int max) {
        this.randomIndex = start;
        this.max = max;
        this.calculatedIndex = ThreadLocalRandom.current().nextInt(0, max);
    }

    /**
     * Check if the calculated index is the same as the given one
     * @return <code>Boolean</code> - <code>True</code> if both indexes are matching, <c>false</c> if they don't
     */
    public boolean matchedIndex() {
        return randomIndex.equals(calculatedIndex);
    }
}
