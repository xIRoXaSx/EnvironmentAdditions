package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.helper.Placeholder;
import net.lizardnetwork.environmentadditions.helper.Random;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModelBiomeEvent extends ModelCondition {
    private final String[] biomes;
    private final ModelCondition condition;
    private final ModelCommand[] commands;
    private final ModelParticle[] particles;
    private final ModelSound[] sounds;

    public ModelBiomeEvent(String[] biomes, ModelCondition condition, ModelCommand[] commands, ModelParticle[] particles, ModelSound[] sounds) {
        super(
            condition.isEnabled(),
            condition.getProbability(),
            condition.getFromTimeInTicks(),
            condition.getUntilTimeInTicks(),
            condition.getWeather(),
            condition.getWorlds(),
            condition.getPermission(),
            condition.getLightCondition(),
            condition.getBlockCondition(),
            condition.getAreaCondition(),
            condition.getWorldGuardCondition(),
            condition.getDateCondition()
        );
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
        return getBiomes().length > 0 && 
            isInWorld(target) &&
            isInSpecifiedBiome(target) &&
            condition.hasPermission(target) &&
            condition.isBetweenTicks(target.getWorld().getTime()) &&
            condition.matchesWeather(getRealWeatherType(target)) &&
            condition.matchesLight(target.getLocation()) &&
            condition.matchesBlock(target.getLocation()) &&
            condition.isInArea(target.getLocation()) &&
            condition.isInRegion(target.getLocation()) &&
            condition.isNotInRegion(target.getLocation()) &&
            condition.isBetweenTime();
    }

    private boolean isInSpecifiedBiome(Player target) {
        String biomePlaceholder = EnvironmentAdditions.getState().getBiomePlaceholder();
        if (Parser.isEmpty(biomePlaceholder)) {
            for (String biome : biomes) {
                if (biome.equalsIgnoreCase(target.getLocation().getBlock().getBiome().name())) {
                    return true;
                }
            }
            return false;
        }

        String papiBiome = new Placeholder(biomePlaceholder).resolve(target).getReplaced();
        for (String biome : biomes) {
            if (biome.equalsIgnoreCase(papiBiome)) {
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
