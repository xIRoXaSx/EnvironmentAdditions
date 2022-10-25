package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.ParticleLoop;

public class ModelParticleLoop {
    private final ParticleLoop version;
    private final int chance;
    private final int radiusInBlocks;

    public ModelParticleLoop(ParticleLoop version, int chance, int radiusInBlocks) {
        this.version = version;
        this.chance = chance;
        this.radiusInBlocks = radiusInBlocks;
    }

    public ParticleLoop getVersion() {
        return version;
    }

    public int getChance() {
        return chance;
    }

    public int getRadiusInBlocks() {
        return radiusInBlocks;
    }
}

