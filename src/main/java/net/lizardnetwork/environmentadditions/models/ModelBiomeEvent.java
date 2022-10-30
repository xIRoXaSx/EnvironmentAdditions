package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.helper.Random;
import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModelBiomeEvent extends ModelCondition implements ICondition {
    private final String[] biomes;
    private final ModelCondition condition;
    private final ModelCommand[] commands;
    private final ModelParticle[] particles;
    private final ModelSound[] sounds;

    public ModelBiomeEvent(String[] biomes, ModelCondition condition, ModelCommand[] commands, ModelParticle[] particles, ModelSound[] sounds) {
        super(condition.isEnabled(), condition.getProbability(), condition.getFromTimeInTicks(), condition.getUntilTimeInTicks(),condition.getWeather(), condition.getPermission());
        this.biomes = biomes;
        this.condition = condition;
        this.commands = commands;
        this.particles = particles;
        this.sounds = sounds;
    }

    /**
     * Check if the event has any value for the given player.
     * Biome entries, ICondition lengths, time, weather and permissions will be checked.
     * @param target Player - The player to run the check for.
     * @return boolean - Valuable or not.
     */
    public boolean hasAnyValueFor(Player target) {
        ModelCommand[] commands = getCommands();
        ModelParticle[] particles = getParticles();
        ModelSound[] sounds = getSounds();
        return getBiomes().length > 0 && isInSpecifiedBiome(target) && condition.hasPermission(target) &&
            condition.isBetweenTicks(target.getPlayerTime()) &&
            condition.matchesWeather(getRealWeatherType(target)) && (
            (commands.length > 0 && anyBasicMatchingCondition(target, commands)) ||
            (particles.length > 0 && anyBasicMatchingCondition(target, particles)) ||
            (sounds.length > 0 && anyBasicMatchingCondition(target, sounds))
        );
    }

    private boolean isInSpecifiedBiome(Player target) {
        for (String biome : biomes) {
            if (biome.equalsIgnoreCase(target.getLocation().getBlock().getBiome().name())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if any of the provided conditions is enabled and if the player has the permission for it.
     * @param player Player - The targeted player.
     * @param conditions ICondition[] - All conditions.
     * @return boolean - True if any condition returned true, false otherwise.
     */
    private boolean anyBasicMatchingCondition(Player player, ICondition[] conditions) {
        for (ICondition condition : conditions) {
            if (condition.isEnabled() && condition.hasPermission(player)) {
                return true;
            }
        }
        return false;
    }

    static void shift(Location src, float x, float y, float z, boolean randomize) {
        if (!randomize) {
            src.add(x, y, z);
            return;
        }
        float sX = x != 0 ? new Random(-x, x).getIntResult() : 0;
        float sY = y != 0 ? new Random(-y, y).getIntResult() : 0;
        float sZ = z != 0 ? new Random(-z, z).getIntResult() : 0;
        src.add(sX, sY, sZ);
    }

    @Override
    public boolean hasPermission(CommandSender target) {
        return super.hasPermission(target);
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
