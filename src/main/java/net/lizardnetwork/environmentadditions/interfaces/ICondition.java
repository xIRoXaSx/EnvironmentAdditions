package net.lizardnetwork.environmentadditions.interfaces;

import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ICondition {
    boolean isEnabled();
    boolean achievedProbability();
    boolean matchesWeather(WeatherType current);
    boolean isBetweenTicks(long current);
    boolean hasPermission(CommandSender target);
    boolean matchesLight(Player target);
    boolean matchesBlock(Player target);
    boolean matchesEveryCondition(Player target);
}
