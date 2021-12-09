package net.lizardnetwork.biomeevents;

import net.lizardnetwork.biomeevents.helper.ChanceCalculation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BiomeParticle {
    private final Particle particle;
    private final Particle.DustOptions options;
    private final double x;
    private final double y;
    private final double z;

    public BiomeParticle(Particle particle, Particle.DustOptions options, double x, double y, double z) {
        this.particle = particle;
        this.options = options;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Spawn a particle for the targeted player
     * @param player <code>Player</code> - The target to spawn the particles for
     * @param count <code>Int<code> - The amount of particles to spawn
     */
    void spawn(Player player, int count) {
        player.spawnParticle(particle, x, y, z, count, options);
    }

    /**
     * Spawn a particle at the targeted location
     * @param location <code>Location</code> - The target to spawn the particles for
     * @param count <code>Int<code> - The amount of particles to spawn
     */
    void spawn(Location location, int count) {
        if (location.getWorld() != null)
            location.getWorld().spawnParticle(particle, x, y, z, count, options);
    }

    /**
     * Spawn V1 particles for the targeted player.
     * This will calculate a cubic region around the player
     * @param player <code>Player</code> - The target to spawn the particles for
     * @param count <code>Int</code> - The amount of particles to spawn
     * @param radius <code>Int</code> - The radius in which the particles should spawn
     * @param chance <code>Int</code> - The chance of each particle to get spawned
     */
    void spawnV1(Player player, int count, int radius, int chance) {
        for (int y = player.getLocation().getBlockY() + radius + 1; y > player.getLocation().getBlockY() - radius; y--) {
            int randomIndex = new ChanceCalculation(0,chance).getRandomInteger();
            ChanceCalculation calculatedChance = new ChanceCalculation(randomIndex, chance);

            if (!calculatedChance.matchedIndex())
                continue;

            for (int x = 0; x < radius + 1; x++) {
                randomIndex = new ChanceCalculation(0, chance).getRandomInteger();
                calculatedChance = new ChanceCalculation(randomIndex, chance);

                if (!calculatedChance.matchedIndex())
                    continue;

                for (int z = 0; z < radius + 1; z++) {
                    // Calculate chance
                    randomIndex = new ChanceCalculation(0, chance).getRandomInteger();
                    calculatedChance = new ChanceCalculation(randomIndex, chance);

                    if (!calculatedChance.matchedIndex())
                        continue;

                    player.spawnParticle(particle, this.x + x, y, this.z + z, count, options);
                    player.spawnParticle(particle, this.x + x, y, this.z - z, count, options);
                    player.spawnParticle(particle, this.x - x, y, this.z + z, count, options);
                    player.spawnParticle(particle, this.x - x, y, this.z - z, count, options);
                }
            }
        }
    }

    /**
     * Spawn V2 particles for the targeted player.
     * This will pick <code>amount</code> random locations in the specified radius and spawns particles there.
     * @param player <code>Player</code> - The target to spawn the particles for
     * @param count <code>Int</code> - The amount of particles to spawn
     * @param radius <code>Int</code> - The radius in which the particles should spawn
     * @param amount <code>Int</code> - The amount of locations to process
     */
    void spawnV2(Player player, int count, int radius, int amount) {
        for (int i = 0; i < amount + 1; i++) {
            double randomX = new ChanceCalculation((double) -radius - 1, (double) radius + 1).getRandomDouble();
            double randomY = new ChanceCalculation((double) -radius - 1, (double) radius + 1).getRandomDouble();
            double randomZ = new ChanceCalculation((double) -radius - 1, (double) radius + 1).getRandomDouble();

            player.spawnParticle(particle, this.x + randomX, this.y + randomY, this.z + randomZ, count, options);
        }
    }
}
