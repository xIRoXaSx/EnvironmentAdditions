package net.lizardnetwork.biomeevents.models;

import org.bukkit.WeatherType;

public class ConditionModel {
    boolean EnableCondition = false;
    WeatherType Weather = WeatherType.CLEAR;
    long FromTimeInTicks = 1000;
    long UntilTimeInTicks = 13000;

    /**
     * Gets the <code>boolean</code> value which represents whether the conditions should be respected or not.
     * @return <code>Boolean</code> - Represents whether the conditions should be respected or not.
     */
    public boolean getEnableCondition() {
        return EnableCondition;
    }

    /**
     * Gets the weather which needs to match
     * @return <code>String</code> - Represents the weather
     */
    public WeatherType getWeatherCondition() {
        return Weather;
    }

    /**
     * Gets the start time in ticks which needs to match
     * @return <code>Long</code> - Represents the start time which needs to be lower or equal to the world time
     */
    public long getStartTimeCondition() {
        return FromTimeInTicks;
    }

    /**
     * Gets the end time in ticks which needs to match
     * @return <code>Long</code> - Represents the end time which needs to be greater or equal to the world time
     */
    public long getEndTimeCondition() {
        return UntilTimeInTicks;
    }
}
