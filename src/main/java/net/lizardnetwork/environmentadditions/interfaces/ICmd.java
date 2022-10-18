package net.lizardnetwork.environmentadditions.interfaces;

import org.bukkit.command.CommandSender;

public interface ICmd {
    boolean execute(CommandSender sender);
    String getCmd();
    String getPermission();
    String[] getAliases();
    String getDescription();
}
