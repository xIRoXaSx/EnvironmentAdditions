package net.lizardnetwork.biomeevents.helper;

import java.util.concurrent.ThreadLocalRandom;

public class ChanceCalculation {
    private final Integer calculatedIndex;
    Integer randomIndex;
    Integer max;

    public ChanceCalculation(int randomIndex, int max) {
        this.randomIndex = randomIndex;
        this.max = max;
        this.calculatedIndex = ThreadLocalRandom.current().nextInt(0, max);
    }

    /**
     * Check if the calculated index is the same as the given one
     * @return Boolean - <c>True</c> if both indexes are matching, <c>false</c> if the don't
     */
    public boolean matchedIndex() {
        return randomIndex.equals(calculatedIndex);
    }
}
