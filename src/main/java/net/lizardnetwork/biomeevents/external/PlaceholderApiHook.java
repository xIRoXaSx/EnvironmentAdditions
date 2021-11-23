package net.lizardnetwork.biomeevents.external;

import me.clip.placeholderapi.PlaceholderAPI;
import net.lizardnetwork.biomeevents.enums.Dependency;
import net.lizardnetwork.biomeevents.helper.DependencyChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.util.Map;

public class PlaceholderApiHook implements Listener {
    private final boolean isEnabled;

    public PlaceholderApiHook() {
        DependencyChecker dependencyChecker = new DependencyChecker();
        Dependency dependency = dependencyChecker.getEnabledDependencies();
        isEnabled = dependency.equals(Dependency.PlaceholderAPI);
    }

    public String getPlaceholder(Player player, String text) {
        if (isEnabled) {
            Bukkit.getLogger().info("Using PAPI!");
            return PlaceholderAPI.setPlaceholders(player, text);
        } else {
            Map<String, String> replacements = Map.of(
                "world", player.getWorld().getName(),
                "player_biome", player.getLocation().getBlock().getBiome().name(),
                "x", String.valueOf(player.getLocation().getBlockX()),
                "player_x", String.valueOf(player.getLocation().getBlockX()),
                "y", String.valueOf(player.getLocation().getBlockY()),
                "player_y", String.valueOf(player.getLocation().getBlockY()),
                "z", String.valueOf(player.getLocation().getBlockZ()),
                "player_z", String.valueOf(player.getLocation().getBlockY()),
                "player", player.getName(),
                "player_name", player.getName()
            );

            Placeholder placeholder = new Placeholder(text, replacements);
            return placeholder.replace();
        }
    }
}
