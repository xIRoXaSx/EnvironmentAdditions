package net.lizardnetwork.environmentadditions.interfaces;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public interface ICmd {
    boolean execute(CommandSender sender, String[] args, Plugin plugin);
    String getCmd();
    String getPermission();
    String[] getAliases();
    String getDescription();
}
