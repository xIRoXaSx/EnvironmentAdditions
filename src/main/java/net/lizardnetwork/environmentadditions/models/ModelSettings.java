package net.lizardnetwork.environmentadditions.models;

public class ModelSettings {
    private final int checkTicks;
    private final String biomePlaceholder;
    private final boolean singleModelMode;

    public ModelSettings(int checkTicks, String biomePlaceholder, boolean singleModelMode) {
        this.checkTicks = checkTicks;
        this.biomePlaceholder = biomePlaceholder;
        this.singleModelMode = singleModelMode;
    }

    public static ModelSettings getDefault() {
        return new ModelSettings(20, "", true);
    }

    public int getCheckTicks() {
        return checkTicks;
    }

    public String getBiomePlaceholder() {
        return biomePlaceholder;
    }

    public boolean isSingleModelMode() {
        return singleModelMode;
    }
}
