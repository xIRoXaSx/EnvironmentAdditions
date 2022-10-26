package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.interfaces.ICondition;

public class ModelBiomeEvent extends ModelCondition implements ICondition {
    private final String[] biomes;
    private final ModelCondition condition;
    private final ModelCommand[] commands;
    private final ModelParticle[] particles;
    private final ModelSound[] sounds;

    public ModelBiomeEvent(String[] biomes, ModelCondition condition, ModelCommand[] commands, ModelParticle[] particles, ModelSound[] sounds) {
        super(condition.isEnabled(), condition.getFromTimeInTicks(), condition.getUntilTimeInTicks(),condition.getWeather(), condition.getPermission());
        this.biomes = biomes;
        this.condition = condition;
        this.commands = commands;
        this.particles = particles;
        this.sounds = sounds;
    }

    public String[] getBiomes() {
        return biomes;
    }

    public ModelCondition getCondition() {
        return condition;
    }

    public ModelCommand[] getCommands() {
        return commands;
    }

    public ModelParticle[] getParticles() {
        return particles;
    }

    public ModelSound[] getSounds() {
        return sounds;
    }
}
