package net.lizardnetwork.environmentadditions.interfaces;

import net.lizardnetwork.environmentadditions.enums.WeatherCondition;

public interface ICondition {
    boolean isEnabled();
    boolean matchesWeather(WeatherCondition current);
    boolean isBetweenTicks(long min, long max, long current);
}
