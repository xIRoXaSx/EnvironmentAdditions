package net.lizardnetwork.biomeevents.external;

import me.clip.placeholderapi.PlaceholderAPI;
import net.lizardnetwork.biomeevents.enums.Dependency;
import net.lizardnetwork.biomeevents.helper.DependencyChecker;
import net.lizardnetwork.biomeevents.helper.Parser;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.util.HashMap;
import java.util.Map;

public class PlaceholderApiHook implements Listener {
    private final boolean isEnabled;
    private boolean forceDefaultCheck = false;
    private String fallbackPlaceholder;

    /**
     * Replace placeholder by either internal checks or PlaceholderAPI if available
     */
    public PlaceholderApiHook() {
        DependencyChecker dependencyChecker = new DependencyChecker();
        Dependency dependency = dependencyChecker.getEnabledDependencies();
        isEnabled = dependency.equals(Dependency.PlaceholderAPI);
    }

    /**
     * Replace placeholder by either internal checks or PlaceholderAPI if installed
     * @param forceDefaultCheck <code>Boolean</code> - <code>True</code> if internal placeholders should be preferred.
     */
    public PlaceholderApiHook(boolean forceDefaultCheck, String fallbackPlaceholder) {
        DependencyChecker dependencyChecker = new DependencyChecker();
        Dependency dependency = dependencyChecker.getEnabledDependencies();
        this.isEnabled = dependency.equals(Dependency.PlaceholderAPI);
        this.forceDefaultCheck = forceDefaultCheck;
        this.fallbackPlaceholder = fallbackPlaceholder;
    }

    /**
     * Get either the corresponding PlaceholderAPI placeholder(s) or the internal one(s)
     * @param player <code>Player</code> - The corresponding player for the placeholder
     * @param text <code>String</code> - The text which contains the placeholder
     * @return <code>String</code> - The string which got the placeholder(s) replaced
     */
    public String getPlaceholder(Player player, String text) {
        if (isEnabled && !forceDefaultCheck) {
            String replacedText = PlaceholderAPI.setPlaceholders(player, text);

            if (replacedText.equals(text) && fallbackPlaceholder != null)
                replacedText = PlaceholderAPI.setPlaceholders(player, fallbackPlaceholder);

            return Parser.getColorizedText(replacedText);
        } else {
            Map<String, String> replacements = Map.of(
                "player", player.getName(),
                "world", player.getWorld().getName(),
                "biome", player.getLocation().getBlock().getBiome().name(),
                "x", String.valueOf(player.getLocation().getBlockX()),
                "y", String.valueOf(player.getLocation().getBlockY()),
                "z", String.valueOf(player.getLocation().getBlockZ())
            );

            Map<String, String> playerReplacements = Map.of(
                "player_name", player.getName(),
                "player_world", player.getWorld().getName(),
                "player_biome", player.getLocation().getBlock().getBiome().name(),
                "player_x", String.valueOf(player.getLocation().getBlockX()),
                "player_y", String.valueOf(player.getLocation().getBlockY()),
                "player_z", String.valueOf(player.getLocation().getBlockY())
            );

            Map<String, String> allReplacements = new HashMap<>();
            allReplacements.putAll(replacements);
            allReplacements.putAll(playerReplacements);

            Placeholder placeholder = new Placeholder(text, allReplacements);
            return placeholder.replace();
        }
    }
}
