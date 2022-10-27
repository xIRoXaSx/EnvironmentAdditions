package net.lizardnetwork.environmentadditions.interfaces;

import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ICondition {
    boolean isEnabled();
    boolean matchesWeather(WeatherType current);
    boolean isBetweenTicks(long current);
    boolean hasPermission(CommandSender target);
    boolean matchesEveryCondition(Player target);
}
