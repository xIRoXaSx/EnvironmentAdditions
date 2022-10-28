package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.WeatherCondition;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModelCondition implements ICondition {
    private final boolean enabled;
    private final long fromTimeInTicks;
    private final long untilTimeInTicks;
    private final WeatherCondition weather;
    private final String permission;

    public ModelCondition(boolean enabled, long fromTimeInTicks, long untilTimeInTicks, WeatherCondition weather, String permission) {
        this.enabled = enabled;
        this.fromTimeInTicks = fromTimeInTicks;
        this.untilTimeInTicks = untilTimeInTicks;
        this.weather = weather;
        this.permission = permission;
    }

    public static ModelCondition getDefault(boolean enabled) {
        if (enabled) {
            return new ModelCondition(true, -1, -1, WeatherCondition.DISABLED,"");
        }
        return new ModelCondition(false, 0, 0, WeatherCondition.CLEAR,"");
    }

    public static boolean hasPermission(CommandSender target, String permission) {
        return Parser.isEmpty(permission) || target.hasPermission(permission);
    }

    /**
     * Check if the provided ICondition matches every condition.
     * @param player Player - The targeted player.
     * @return boolean - True if every condition returned true, false otherwise.
     */
    @Override
    public boolean matchesEveryCondition(Player player) {
        return isEnabled() && hasPermission(player) &&
            matchesWeather(player.getPlayerWeather()) &&
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
    public boolean matchesWeather(WeatherType current) {
        return weather.equals(WeatherCondition.DISABLED) || weather.name().equals(current.name());
    }

    @Override
    public boolean isBetweenTicks(long current) {
        return fromTimeInTicks < 0 || untilTimeInTicks < 0 || current >= fromTimeInTicks && current <= untilTimeInTicks;
    }

    public long getFromTimeInTicks() {
        return fromTimeInTicks;
    }

    public long getUntilTimeInTicks() {
        return untilTimeInTicks;
    }

    public WeatherCondition getWeather() {
        return weather;
    }

    public String getPermission() {
        return permission;
    }
}
