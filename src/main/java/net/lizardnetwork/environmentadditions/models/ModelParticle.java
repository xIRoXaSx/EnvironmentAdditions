package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import org.bukkit.Particle;

public class ModelParticle extends ModelCondition implements ICondition {
    private final Particle particle;
    private final String hexColor;
    private final int size;
    private final int numParticles;
    private final ModelCondition condition;
    private final ModelParticleAnimation animation;

    public ModelParticle(Particle particle, String hexColor, int size, int numParticles, ModelCondition condition, ModelParticleAnimation animation) {
        super(condition.isEnabled(), condition.getFromTimeInTicks(), condition.getUntilTimeInTicks(),condition.getWeather(), condition.getPermission());
        this.particle = particle;
        this.hexColor = hexColor;
        this.size = size;
        this.numParticles = numParticles;
        this.condition = condition;
        this.animation = animation;
    }

    public Particle getParticle() {
        return particle;
    }

    public String getHexColor() {
        return hexColor;
    }

    public int getSize() {
        return size;
    }

    public int getNumParticles() {
        return numParticles;
    }

    public ModelCondition getCondition() {
        return condition;
    }

    public ModelParticleAnimation getAnimation() {
        return animation;
    }
}
