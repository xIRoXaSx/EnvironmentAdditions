package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.EProbability;
import net.lizardnetwork.environmentadditions.enums.EWeatherCondition;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.helper.Probability;
import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import net.lizardnetwork.environmentadditions.interfaces.IRandomized;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModelCondition implements ICondition, IRandomized {
    private final boolean enabled;
    private final int probability;
    private final long fromTimeInTicks;
    private final long untilTimeInTicks;
    private final EWeatherCondition weather;
    private final String permission;

    public ModelCondition(boolean enabled, int probability, long fromTimeInTicks, long untilTimeInTicks, EWeatherCondition weather, String permission) {
        this.enabled = enabled;
        this.probability = probability;
        this.fromTimeInTicks = fromTimeInTicks;
        this.untilTimeInTicks = untilTimeInTicks;
        this.weather = weather;
        this.permission = permission;
    }

    public static ModelCondition getDefault(boolean enabled) {
        if (enabled) {
            return new ModelCondition(true, 1, -1, -1, EWeatherCondition.DISABLED,"");
        }
        return new ModelCondition(false, -1, 0, 0, EWeatherCondition.CLEAR,"");
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
            isBetweenTicks(player.getPlayerTime());
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
        return !world.isClearWeather() || world.isThundering() ? WeatherType.DOWNFALL : WeatherType.CLEAR;
    }

    public int getProbability() {
        return probability;
    }

    public long getFromTimeInTicks() {
        return fromTimeInTicks;
    }

    public long getUntilTimeInTicks() {
        return untilTimeInTicks;
    }

    public EWeatherCondition getWeather() {
        return weather;
    }

    public String getPermission() {
        return permission;
    }
}
