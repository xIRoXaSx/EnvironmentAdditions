package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.interfaces.ICondition;

public class ModelBiomeEvent extends ModelCondition implements ICondition {
    private final ModelCondition condition;
    private final ModelCommand[] commands;
    private final ModelParticle[] particles;
    private final ModelSound[] sounds;

    public ModelBiomeEvent(ModelCondition condition, ModelCommand[] commands, ModelParticle[] particles, ModelSound[] sounds) {
        super(condition.isEnabled(), condition.getFromTimeInTicks(), condition.getUntilTimeInTicks(),condition.getWeather(), condition.getPermission());
        this.condition = condition;
        this.commands = commands;
        this.particles = particles;
        this.sounds = sounds;
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
