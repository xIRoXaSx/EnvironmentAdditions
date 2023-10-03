package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.enums.EDependency;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Resolve {
    /**
     * Get dependencies from the installed plugins.
     * @return int - The dependency value which represents the installed plugins.
     */
    public static int hookableDependencies() {
        int retValue = EDependency.None.getValue();
        String[] plugins = {"PlaceholderAPI", "WorldGuard"};
        Plugin[] availablePlugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : availablePlugins) {
            for (String lookup : plugins) {
                if (plugin.getName().equals(lookup)) {
                    retValue += EDependency.valueOf(lookup).getValue();
                    break;
                }
            }
        }
        return retValue;
    }
}