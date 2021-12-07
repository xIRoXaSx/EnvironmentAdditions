package net.lizardnetwork.biomeevents.helper;

import java.util.concurrent.ThreadLocalRandom;

public class ChanceCalculation {
    private Integer calculatedIndex = null;
    private Double calculatedIndexDouble = null;
    Integer start = null;
    Double startDouble = null;
    Integer max = null;
    Double maxDouble = null;

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
            this.start = max;
            this.max = start;
        }

        this.calculatedIndex = ThreadLocalRandom.current().nextInt(this.start, this.max);
    }

    /**
     * A chance calculation between a <code>start</code> int and <code>max</code> int
     * @param start <code>Int</code> - The lowest possible value (inclusive)
     * @param max <code>Int</code> - The max bound (exclusive)
     */
    public ChanceCalculation(double start, double max) {
        this.startDouble = start;
        this.maxDouble = max;

        if (startDouble.equals(maxDouble)) {
            this.calculatedIndexDouble = start;
            return;
        }

        // Change values if swapped
        if (start > max) {
            this.startDouble = max;
            this.maxDouble = start;
        }

        this.calculatedIndexDouble = ThreadLocalRandom.current().nextDouble(this.startDouble, this.maxDouble);
    }

    /**
     * Check if the calculated index is the same as the given one
     * @return <code>Boolean</code> - <code>True</code> if both indexes are matching, <c>false</c> if they don't
     */
    public boolean matchedIndex() {
        if (calculatedIndex != null)
            return start.equals(calculatedIndex);

        return startDouble.equals(calculatedIndexDouble);
    }

    /**
     * Get the random Integer
     * @return <code>Integer</code> - The calculated random number
     */
    public Integer getRandomInteger() {
        return calculatedIndex;
    }

    /**
     * Get the random Double
     * @return <code>Double</code> - The calculated random number
     */
    public Double getRandomDouble() {
        return calculatedIndexDouble;
    }
}
