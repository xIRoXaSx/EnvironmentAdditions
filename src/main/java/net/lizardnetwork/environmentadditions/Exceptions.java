package net.lizardnetwork.environmentadditions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Exceptions {
    public static void Fatal(String msg) {
        Plugin plugin = EnvironmentAdditions.getInstance();
        plugin.getLogger().warning(msg);
        Bukkit.getPluginManager().disablePlugin(EnvironmentAdditions.getInstance());
    }
}
