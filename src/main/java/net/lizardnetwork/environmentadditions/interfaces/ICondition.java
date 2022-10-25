package net.lizardnetwork.environmentadditions.interfaces;

import org.bukkit.WeatherType;

public interface ICondition {
    boolean isEnabled();
    boolean matchesWeather(WeatherType current);
    boolean isBetweenTicks(long min, long max, long current);
}
