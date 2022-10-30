package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.EParticleLoop;
import net.lizardnetwork.environmentadditions.enums.EProbability;
import net.lizardnetwork.environmentadditions.helper.Probability;
import net.lizardnetwork.environmentadditions.interfaces.IRandomized;

public class ModelParticleLoop implements IRandomized {
    private final EParticleLoop version;
    private final int probability;
    private final int radiusInBlocks;

    public ModelParticleLoop(EParticleLoop version, int probability, int radiusInBlocks) {
        this.version = version;
        this.probability = probability;
        this.radiusInBlocks = radiusInBlocks;
    }

    @Override
    public EProbability achievedProbability() {
        return new Probability(probability).achievedProbability();
    }

    public EParticleLoop getVersion() {
        return version;
    }

    public int getProbability() {
        return probability;
    }

    public int getRadiusInBlocks() {
        return radiusInBlocks;
    }
}

