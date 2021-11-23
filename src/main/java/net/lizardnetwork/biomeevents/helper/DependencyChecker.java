package net.lizardnetwork.biomeevents.helper;

import net.lizardnetwork.biomeevents.enums.Dependency;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import java.util.Arrays;

public class DependencyChecker {
    /**
     * Get dependencies (from installed plugins)
     * @return Dependencies
     */
    public Dependency getEnabledDependencies() {
        Dependency returnValue = Dependency.None;
        Plugin[] enabledPlugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(Plugin::isEnabled).toArray(Plugin[]::new);

        // Check if PlaceholderAPI is enabled
        if (Arrays.stream(enabledPlugins).anyMatch(x -> x.getName().equals("PlaceholderAPI")) ) {
            returnValue = Dependency.PlaceholderAPI;
        }

        return returnValue;
    }
}
