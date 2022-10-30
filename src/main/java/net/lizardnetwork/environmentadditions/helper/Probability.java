package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.enums.EProbability;

public class Probability {
    private final int probability;

    public Probability(int probability) {
        this.probability = probability;
    }

    /**
     * Whether the probability has been achieved.
     * @return Probability - Whether probability achieved, not achieved or disabled.
     */
    public EProbability achievedProbability() {
        if (probability < 1) {
            return EProbability.DISABLED;
        }
        return new Random(0, probability).getIntResult() == 0 ? EProbability.ACHIEVED : EProbability.NOT_ACHIEVED;
    }
}
