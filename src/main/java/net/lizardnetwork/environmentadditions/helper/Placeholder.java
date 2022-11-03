package net.lizardnetwork.environmentadditions.helper;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.enums.EDependency;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {
    private final Character placeholderSeparatorChar = '%';
    private final String value;
    private String replaced;
    private Map<String, String> replacements = new HashMap<>();

    public Placeholder(String value, Map<String, String> replacements) {
        this.value = value;
        this.replaced = value;
        this.replacements = replacements;
    }

    public Placeholder(String value) {
        this.value = value;
        this.replaced = value;
    }

    /**
     * Sets the <code>replaced</code> field with the already provided information.
     * @return Placeholder - The current placeholder instance.
     */
    private Placeholder replace() {
        // Iterate through all key names to use in regex pattern
        for (String replacement : replacements.keySet()) {
            Pattern pattern = Pattern.compile(placeholderSeparatorChar + replacement + placeholderSeparatorChar, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(value);

            if (matcher.find()) {
                replaced = replaced.replaceAll(placeholderSeparatorChar + replacement + placeholderSeparatorChar, replacements.get(replacement));
            }
        }
        return this;
    }

    /**
     * Replace <code>value</code> with all the corresponding pairs of <code>replacements</code>.
     * @return Placeholder - The current placeholder instance.
     */
    public Placeholder resolve(Player target) {
        Pattern pattern = Pattern.compile(placeholderSeparatorChar + "(.*?)" + placeholderSeparatorChar, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);

        while (matcher.find()) {
            String replacement = resolvePlaceholder(target, matcher.group(0), EDependency.PlaceholderAPI);
            // Edge case %player%, resolve as the target's username.
            if (matcher.group(1).equalsIgnoreCase("player")) {
                replaced = replaced.replaceAll(matcher.group(0), target.getName());
                continue;
            }
            replaced = replaced.replaceAll(matcher.group(0), replacement);
        }
        return this;
    }

    /**
     * Get either the corresponding PlaceholderAPI placeholder(s) or the internal one(s).
     * @param target Player - The targeted player for the placeholder(s).
     * @param text String - The text which contains the placeholder.
     * @param defaultSystem Dependency - The placeholder system to use.
     * @return String - The replaced string.
     */
    public String resolvePlaceholder(Player target, String text, EDependency defaultSystem) {
        EDependency dependency = EnvironmentAdditions.getState().getDependency();
        if (dependency.equals(EDependency.PlaceholderAPI) && !defaultSystem.equals(EDependency.None)) {
            // Return the replaced string.
            // If PlaceholderAPI could not resolve the placeholder(s), the return value
            // will contain the raw message instead of trying to parse them internally in favor of performance.
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(target, text);
        }

        // Only use internal placeholders.
        // player_x placeholders are now obsolete in favor of performance.
        Placeholder placeholder = new Placeholder(text, Map.of(
            "player", target.getName(),
            "world", target.getWorld().getName(),
            "biome", target.getLocation().getBlock().getBiome().name(),
            "player_biome", target.getLocation().getBlock().getBiome().name(),
            "x", String.valueOf(target.getLocation().getBlockX()),
            "y", String.valueOf(target.getLocation().getBlockY()),
            "z", String.valueOf(target.getLocation().getBlockZ())
        ));
        return placeholder.replace().getReplaced();
    }

    /**
     * Get the replaced String of value.
     * @return String - Value, replaced with available replacements.
     */
    public String getReplaced() {
        return replaced;
    }
}
