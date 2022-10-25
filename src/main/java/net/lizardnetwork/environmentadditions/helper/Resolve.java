package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.enums.Dependency;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Resolve {
    /**
     * Get dependencies from the installed plugins.
     * @return Dependency - The enum which represents the installed plugin dependency.
     */
    public static Dependency resolveDependencies() {
        String lookup = "PlaceholderAPI";
        Plugin[] availablePlugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : availablePlugins) {
            if (!plugin.isEnabled() || !plugin.getName().equals(lookup)) {
                continue;
            }
            return Dependency.PlaceholderAPI;
        }
        return Dependency.None;
    }
}