package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;

public class ModelCondition implements ICondition {
    private final boolean enabled;
    private final long fromTimeInTicks;
    private final long untilTimeInTicks;
    private final WeatherType weather;
    private final String permission;

    public ModelCondition(boolean enabled, long fromTimeInTicks, long untilTimeInTicks, WeatherType weather, String permission) {
        this.enabled = enabled;
        this.fromTimeInTicks = fromTimeInTicks;
        this.untilTimeInTicks = untilTimeInTicks;
        this.weather = weather;
        this.permission = permission;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean matchesWeather(WeatherType current) {
        return weather.equals(current);
    }

    @Override
    public boolean isBetweenTicks(long min, long max, long current) {
        return current <= max && current >= min;
    }

    public static boolean hasPermission(CommandSender target, String permission) {
        return Parser.isEmpty(permission) || target.hasPermission(permission);
    }

    public long getFromTimeInTicks() {
        return fromTimeInTicks;
    }

    public long getUntilTimeInTicks() {
        return untilTimeInTicks;
    }

    public WeatherType getWeather() {
        return weather;
    }

    public String getPermission() {
        return permission;
    }
}
