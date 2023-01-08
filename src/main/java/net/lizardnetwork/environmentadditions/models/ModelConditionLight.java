package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.ELightSource;

public class ModelConditionLight {
    private final ELightSource type;
    private final int minLevel;
    private final int maxLevel;

    public ModelConditionLight(ELightSource type, int minLvl, int maxLvl) {
        this.type = type;
        this.minLevel = minLvl;
        this.maxLevel = maxLvl;
    }

    public ELightSource getType() {
        return type;
    }

    public boolean isBetween(byte value) {
        int max = maxLevel;
        if (maxLevel == -1) {
            max = 16;
        }
        return value >= minLevel && value <= max;
    }
}
