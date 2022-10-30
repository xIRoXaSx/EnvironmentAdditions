package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.EParticleLoop;
import net.lizardnetwork.environmentadditions.enums.EProbability;
import net.lizardnetwork.environmentadditions.helper.Probability;
import net.lizardnetwork.environmentadditions.interfaces.IRandomized;

public class ModelParticleLoop implements IRandomized {
    private final EParticleLoop type;
    private final int probability;
    private final int radiusInBlocks;

    public ModelParticleLoop(EParticleLoop type, int probability, int radiusInBlocks) {
        this.type = type;
        this.probability = probability;
        this.radiusInBlocks = radiusInBlocks;
    }

    @Override
    public EProbability getAchievedProbability() {
        return new Probability(probability).achievedProbability();
    }

    public EParticleLoop getType() {
        return type;
    }

    public int getProbability() {
        return probability;
    }

    public int getRadiusInBlocks() {
        return radiusInBlocks;
    }
}

