package net.lizardnetwork.biomeevents.models;

import org.bukkit.WeatherType;

public class ConditionModel {
    private boolean EnableCondition = false;
    private WeatherType Weather = WeatherType.CLEAR;
    private long FromTimeInTicks = 1000;
    private long UntilTimeInTicks = 13000;

    /**
     * Get whether the condition block should be respected or not
     * @return <code>Boolean</code> - Whether conditions should be checked or not
     */
    public boolean isEnabled() {
        return EnableCondition;
    }

    /**
     * Set whether the condition block should be respected or not
     * @param enableCondition <code>Boolean</code> - Whether conditions should be checked or not
     */
    public void setIsEnabled(boolean enableCondition) {
        EnableCondition = enableCondition;
    }

    /**
     * Get the weather needed in order to pass the check
     * @return <code>WeatherType</code> - The weather needed to pass the check
     */
    public WeatherType getWeather() {
        return Weather;
    }

    /**
     * Set the weather needed in order to pass the check
     * @param weather <code>WeatherType</code> - The weather needed to pass the check
     */
    public void setWeather(WeatherType weather) {
        Weather = weather;
    }

    /**
     * Get the minimum time in ticks when an operation should start
     * @return <code>Long</code> - The time in ticks
     */
    public long getFromTimeInTicks() {
        return FromTimeInTicks;
    }

    /**
     * Set the minimum time in ticks when an operation should start
     * @param fromTimeInTicks <code>Long</code> - The time in ticks
     */
    public void setFromTimeInTicks(long fromTimeInTicks) {
        FromTimeInTicks = fromTimeInTicks;
    }

    /**
     * Get the maximum time in ticks when an operation should stop
     * @return <code>Long</code> - The time in ticks
     */
    public long getUntilTimeInTicks() {
        return UntilTimeInTicks;
    }

    /**
     * Set the maximum time in ticks when an operation should stop
     * @param untilTimeInTicks <code>Long</code> - The time in ticks
     */
    public void setUntilTimeInTicks(long untilTimeInTicks) {
        UntilTimeInTicks = untilTimeInTicks;
    }
}
