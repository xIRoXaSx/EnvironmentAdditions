package net.lizardnetwork.environmentadditions.interfaces;

import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ICondition {
    boolean isEnabled();
    boolean achievedProbability();
    boolean matchesWeather(WeatherType current);
    boolean isBetweenTicks(long current);
    boolean hasPermission(CommandSender target);
    boolean matchesLight(Location target);
    boolean matchesBlock(Location target);
    boolean isInArea(Location target);
    boolean isInRegion(Location target);
    boolean isNotInRegion(Location target);
    boolean matchesEveryCondition(Player target);
    boolean isWorldGuardConfigured();
}
