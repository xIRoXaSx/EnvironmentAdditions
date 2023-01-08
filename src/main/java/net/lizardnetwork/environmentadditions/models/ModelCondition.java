package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.ELightSource;
import net.lizardnetwork.environmentadditions.enums.EProbability;
import net.lizardnetwork.environmentadditions.enums.EWeatherCondition;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.helper.Probability;
import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import net.lizardnetwork.environmentadditions.interfaces.IRandomized;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Objects;

public class ModelCondition implements ICondition, IRandomized {
    private final boolean enabled;
    private final int probability;
    private final int fromTimeInTicks;
    private final int untilTimeInTicks;
    private final EWeatherCondition weather;
    private final String permission;
    private final ModelConditionBlock blockCondition;
    private final ModelConditionLight lightCondition;

    public ModelCondition(
            boolean enabled,
            int probability,
            int fromTimeInTicks,
            int untilTimeInTicks,
            EWeatherCondition weather,
            String permission,
            ModelConditionLight lightCondition,
            ModelConditionBlock blockCondition
        ) {
        this.enabled = enabled;
        this.probability = probability;
        this.fromTimeInTicks = fromTimeInTicks;
        this.untilTimeInTicks = untilTimeInTicks;
        this.weather = weather;
        this.permission = permission;
        this.lightCondition = lightCondition;
        this.blockCondition = blockCondition;
    }

    public static ModelCondition getDefault(boolean enabled) {
        ModelConditionBlock condBlock = new ModelConditionBlock(Material.VOID_AIR.toString(), new ModelPosOffset(0,0,0));
        ModelConditionLight condLight = new ModelConditionLight(ELightSource.GENERIC, -1, -1);
        if (enabled) {
            return new ModelCondition(true, 1, -1, -1, EWeatherCondition.DISABLED, "", condLight, condBlock);
        }

        condBlock = new ModelConditionBlock(Material.GRASS_BLOCK.toString(), new ModelPosOffset(1,1,1));
        condLight = new ModelConditionLight(ELightSource.GENERIC, 0, 15);
        return new ModelCondition(false, -1, 0, 0, EWeatherCondition.CLEAR, "", condLight, condBlock);
    }

    public static boolean hasPermission(CommandSender target, String permission) {
        return Parser.isEmpty(permission) || target.hasPermission(permission);
    }

    @Override
    public EProbability getAchievedProbability() {
        return new Probability(probability).achievedProbability();
    }

    /**
     * Check if the provided ICondition matches every condition.
     * @param player Player - The targeted player.
     * @return boolean - True if every condition returned true, false otherwise.
     */
    @Override
    public boolean matchesEveryCondition(Player player) {
        return isEnabled() &&
            hasPermission(player) && achievedProbability() &&
            matchesWeather(getRealWeatherType(player)) &&
            isBetweenTicks(player.getWorld().getTime()) &&
            matchesLight(player) &&
            matchesBlock(player);
    }

    @Override
    public boolean hasPermission(CommandSender target) {
        return Parser.isEmpty(permission) || target.hasPermission(permission);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean achievedProbability() {
        return getAchievedProbability().equals(EProbability.DISABLED) ||
            getAchievedProbability().equals(EProbability.ACHIEVED);
    }

    @Override
    public boolean matchesWeather(WeatherType current) {
        return weather.equals(EWeatherCondition.DISABLED) || weather.name().equals(current.name());
    }

    @Override
    public boolean isBetweenTicks(long current) {
        return fromTimeInTicks < 0 || untilTimeInTicks < 0 || current >= fromTimeInTicks && current <= untilTimeInTicks;
    }

    public WeatherType getRealWeatherType(Player target) {
        WeatherType weather = target.getPlayerWeather();
        if (weather != null) {
            return weather;
        }

        World world = target.getWorld();
        return world.isClearWeather() ? WeatherType.CLEAR : WeatherType.DOWNFALL;
    }

    public boolean matchesLight(Player target) {
        Block block = target.getLocation().getBlock();
        byte light = 0;
        switch (lightCondition.getType()) {
            case SKY -> light = block.getLightFromSky();
            case BLOCK -> light = block.getLightFromBlocks();
            case GENERIC -> light = block.getLightLevel();
        }
        return lightCondition.isBetween(light);
    }

    public boolean matchesBlock(Player target) {
        String material = blockCondition.getMaterial();
        if (Objects.equals(material, Material.VOID_AIR.toString()) || Objects.equals(material, "")) {
            return true;
        }

        ModelPosOffset offset = blockCondition.getPosOffset();
        Location loc = target.getLocation().add(offset.getRelativeX(), offset.getRelativeY(), offset.getRelativeZ());
        return Objects.equals(loc.getBlock().getType().toString(), material);
    }

    public int getProbability() {
        return probability;
    }

    public int getFromTimeInTicks() {
        return fromTimeInTicks;
    }

    public int getUntilTimeInTicks() {
        return untilTimeInTicks;
    }

    public EWeatherCondition getWeather() {
        return weather;
    }

    public String getPermission() {
        return permission;
    }

    public ModelConditionLight getLightCondition() {
        return lightCondition;
    }

    public ModelConditionBlock getBlockCondition() {
        return blockCondition;
    }
}
