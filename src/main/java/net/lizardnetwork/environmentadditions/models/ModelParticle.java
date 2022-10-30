package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.EProbability;
import net.lizardnetwork.environmentadditions.helper.Probability;
import net.lizardnetwork.environmentadditions.helper.Random;
import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import net.lizardnetwork.environmentadditions.interfaces.IModelExecutor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ModelParticle extends ModelCondition implements ICondition, IModelExecutor {
    private final Particle particle;
    private final Color color;
    private final int size;
    private final int numParticles;
    private final ModelCondition condition;
    private final ModelParticleAnimation animation;

    public ModelParticle(Particle particle, Color color, int size, int numParticles, ModelCondition condition, ModelParticleAnimation animation) {
        super(condition.isEnabled(), condition.getProbability() , condition.getFromTimeInTicks(), condition.getUntilTimeInTicks(),condition.getWeather(), condition.getPermission());
        this.particle = particle;
        this.color = color;
        this.size = size;
        this.numParticles = numParticles;
        this.condition = condition;
        this.animation = animation;
    }

    @Override
    public void execute(Player target) {
        ModelParticleLoop loopOpts = animation.getLoopOption();
        switch (loopOpts.getType()) {
            case STATIC -> spawnStatic(target);
            case CUBIC -> spawnCubic(target, loopOpts);
            case RANDOM -> spawnRandom(target, loopOpts);
        }
    }

    /**
     * Spawn particles of type "STATIC".
     * @param target - The target to spawn the particles for.
     */
    private void spawnStatic(Player target) {
        float relX = animation.getRelOffsetX();
        float relY = animation.getRelOffsetY();
        float relZ = animation.getRelOffsetZ();
        float vdd = animation.getViewDirectionDistance();
        Location src = target.getLocation();
        Particle.DustOptions dustOpts = getDustOptions();
        if (vdd > 0) {
            src = calculateViewDirection(target, vdd);
        }
        ModelBiomeEvent.shift(src, relX, relY, relZ, false);
        target.spawnParticle(particle, src, numParticles, dustOpts);
    }

    /**
     * Spawn particles of type "CUBIC" - DEPRECATED.
     * @param target - The target to spawn the particles for.
     * @param loopOpts - The ModelParticleLoop.
     */
    private void spawnCubic(Player target, ModelParticleLoop loopOpts) {
        Location src = target.getLocation();
        Particle.DustOptions dustOpts = getDustOptions();
        int radius = loopOpts.getRadiusInBlocks();
        int probability = loopOpts.getProbability();
        int srcX = src.getBlockX();
        int srcY = src.getBlockY();
        int srcZ = src.getBlockZ();
        for (int y = srcY + radius + 1; y > srcY - radius; y--) {
            EProbability prob = new Probability(probability).achievedProbability();
            if (!prob.equals(EProbability.ACHIEVED)) {
                continue;
            }

            for (int x = srcX + radius + 1; x > srcX - radius; x--) {
                prob = new Probability(probability).achievedProbability();
                if (!prob.equals(EProbability.ACHIEVED)) {
                    continue;
                }

                for (int z = srcZ + radius + 1; z > srcZ - radius ; z--) {
                    prob = new Probability(probability).achievedProbability();
                    if (!prob.equals(EProbability.ACHIEVED)) {
                        continue;
                    }
                    target.spawnParticle(particle, x, y, z, numParticles, dustOpts);
                }
            }
        }
    }

    /**
     * Spawn particles of type "RANDOM".
     * @param target - The target to spawn the particles for.
     * @param loopOpts - The ModelParticleLoop.
     */
    private void spawnRandom(Player target, ModelParticleLoop loopOpts) {
        // Chance will be handled differently here.
        // Instead of the chance that a particle will spawn, it will be used as an iterator.
        int chance = loopOpts.getProbability();
        int radius = loopOpts.getRadiusInBlocks();
        Location src = target.getLocation();
        Particle.DustOptions dustOpts = getDustOptions();
        for (int i = 0; i < chance; i++) {
            double x = new Random(-radius, radius + 1).getFloatResult();
            double y = new Random(-radius, radius + 1).getFloatResult();
            double z = new Random(-radius, radius + 1).getFloatResult();
            target.spawnParticle(particle, src.getX() + x, src.getY()+ y, src.getZ() + z, numParticles, dustOpts);
        }
    }

    private Particle.DustOptions getDustOptions() {
        return particle.equals(Particle.REDSTONE) ? new Particle.DustOptions(color, size) : null;
    }

    private Location calculateViewDirection(Player target, float vdd) {
        Location eyeLocation = target.getEyeLocation();
        Vector nv = eyeLocation.getDirection().normalize();
        double x = vdd * nv.getX() + eyeLocation.getX();
        double y = vdd * nv.getY() + eyeLocation.getY();
        double z = vdd * nv.getZ() + eyeLocation.getZ();
        return new Location(target.getWorld(), x, y, z);
    }

    public Particle getParticle() {
        return particle;
    }

    public Color getColor() {
        return color;
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
