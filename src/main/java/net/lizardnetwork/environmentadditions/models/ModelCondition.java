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
    private final String[] worlds;
    private final String permission;
    private final ModelConditionBlock blockCondition;
    private final ModelConditionLight lightCondition;
    private final ModelConditionArea areaCondition;
    private final ModelConditionWorldGuard wgCondition;
    private final ModelConditionDate dateCondition;

    public ModelCondition(
            boolean enabled,
            int probability,
            int fromTimeInTicks,
            int untilTimeInTicks,
            EWeatherCondition weather,
            String[] worlds,
            String permission,
            ModelConditionLight lightCondition,
            ModelConditionBlock blockCondition,
            ModelConditionArea areaCondition,
            ModelConditionWorldGuard wgCondition,
            ModelConditionDate dateCondition
        ) {
        this.enabled = enabled;
        this.probability = probability;
        this.fromTimeInTicks = fromTimeInTicks;
        this.untilTimeInTicks = untilTimeInTicks;
        this.weather = weather;
        this.worlds = worlds;
        this.permission = permission;
        this.lightCondition = lightCondition;
        this.blockCondition = blockCondition;
        this.areaCondition = areaCondition;
        this.wgCondition = wgCondition;
        this.dateCondition = dateCondition;
    }

    public static ModelCondition getDefault(boolean enabled) {
        ModelConditionBlock condBlock = new ModelConditionBlock(Material.VOID_AIR.toString(), new ModelPosOffset(0,0,0));
        ModelConditionLight condLight = new ModelConditionLight(ELightSource.GENERIC, -1, -1);
        ModelConditionWorldGuard wgCondition = new ModelConditionWorldGuard(false, new String[]{"global"}, new String[]{"private"});
        ModelConditionDate dateCondition = new ModelConditionDate("", "");
        ModelConditionArea condArea = new ModelConditionArea(
            false,
            new ModelPosOffset(0, 0, 0),
            new ModelPosOffset(0, 0, 0)
        );
        if (enabled) {
            return new ModelCondition(
                true,
                1,
                -1,
                -1,
                EWeatherCondition.DISABLED,
                new String[0],
                "",
                condLight,
                condBlock,
                condArea,
                new ModelConditionWorldGuard(false, new String[0], new String[0]),
                dateCondition
            );
        }

        condBlock = new ModelConditionBlock(Material.GRASS_BLOCK.toString(), new ModelPosOffset(1, 1, 1));
        condLight = new ModelConditionLight(ELightSource.GENERIC, 0, 15);
        condArea = new ModelConditionArea(
            false,
            new ModelPosOffset(-100, -64, -100),
            new ModelPosOffset(100, 320, 100)
        );
        return new ModelCondition(false, -1, 0, 0, EWeatherCondition.CLEAR, new String[0], "", condLight, condBlock, condArea, wgCondition, dateCondition);
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
            isInWorld(player) &&
            hasPermission(player) && 
            achievedProbability() &&
            matchesWeather(getRealWeatherType(player)) &&
            isBetweenTicks(player.getWorld().getTime()) &&
            matchesLight(player.getLocation()) &&
            matchesBlock(player.getLocation()) &&
            isInArea(player.getLocation()) &&
            isInRegion(player.getLocation()) &&
            isNotInRegion(player.getLocation()) &&
            isBetweenTime();
    }

    @Override
    public boolean hasPermission(CommandSender target) {
        return Parser.isEmpty(permission) || target.hasPermission(permission);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isInWorld(Player target) {
        if (this.worlds == null || this.worlds.length == 0) {
            return true;
        }

        for (String world : this.worlds) {
            if (target.getWorld().getName().equals(world)) {
                return true;
            }
        }
        return false;
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

    public boolean matchesLight(Location target) {
        Block block = target.getBlock();
        byte light = 0;
        switch (lightCondition.getType()) {
            case SKY -> light = block.getLightFromSky();
            case BLOCK -> light = block.getLightFromBlocks();
            case GENERIC -> light = block.getLightLevel();
        }
        return lightCondition.isBetween(light);
    }

    public boolean matchesBlock(Location target) {
        String material = blockCondition.getMaterial();
        if (Objects.equals(material, Material.VOID_AIR.toString()) || Objects.equals(material, "")) {
            return true;
        }

        ModelPosOffset offset = blockCondition.getPosOffset();
        Location loc = target.add(offset.getRelativeX(), offset.getRelativeY(), offset.getRelativeZ());
        return Objects.equals(loc.getBlock().getType().toString(), material);
    }

    public boolean isInArea(Location target) {
        return areaCondition.isInArea(target);
    }

    public boolean isInRegion(Location target) {
        return wgCondition.isInside(target);
    }

    public boolean isNotInRegion(Location target) {
        return wgCondition.isOutside(target);
    }

    public boolean isBetweenTime() {
        return dateCondition.isBetween();
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

    public String[] getWorlds() {
        return worlds;
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

    public ModelConditionArea getAreaCondition() {
        return areaCondition;
    }

    public ModelConditionWorldGuard getWorldGuardCondition() {
        return wgCondition;
    }

    public ModelConditionDate getDateCondition() {
        return dateCondition;
    }
}
