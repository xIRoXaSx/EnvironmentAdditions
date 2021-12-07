package net.lizardnetwork.biomeevents.external;

import me.clip.placeholderapi.PlaceholderAPI;
import net.lizardnetwork.biomeevents.helper.Parser;
import org.bukkit.entity.Player;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {
    private Map<String, String> replacements = null;
    private String placeholderString;

    public Placeholder(String placeholderString, Map<String, String> replacements) {
        this.placeholderString = placeholderString;
        this.replacements = replacements;
    }

    public Placeholder(String placeholderString) {
        this.placeholderString = placeholderString;
    }

    /**
     * Replace the placeholder text with the corresponding replacements
     * @return <code>String</code> - Containing the replacements instead of placeholders if applicable
     */
    public String replace() {
        Character placeholderSeparatorChar = '%';

        // Iterate through all key names to use in regex pattern
        for (String replacement : replacements.keySet()) {
            Pattern pattern = Pattern.compile(placeholderSeparatorChar + replacement + placeholderSeparatorChar, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(placeholderString);

            if (matcher.find()) {
                placeholderString = placeholderString.replaceAll(placeholderSeparatorChar + replacement + placeholderSeparatorChar, replacements.get(replacement));
            }
        }

        return Parser.getColorizedText(placeholderString);
    }

    /**
     * Replace the placeholder text with the corresponding PlaceholderAPI parts
     * @return <code>String</code> - Containing the replacements instead of placeholders if applicable
     */
    public String replaceFromPlaceholderApi(Player player) {
        Character placeholderSeparatorChar = '%';
        Pattern pattern = Pattern.compile(placeholderSeparatorChar + "(.*?)" + placeholderSeparatorChar, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(placeholderString);
        PlaceholderApiHook placeholderApiHook = new PlaceholderApiHook();

        while (matcher.find()) {
            String replacement = placeholderApiHook.getPlaceholder(player, matcher.group(0));

            if (matcher.group(1).equalsIgnoreCase("player")) {
                placeholderString = placeholderString.replaceAll(matcher.group(0), player.getName());
                continue;
            }

            placeholderString = placeholderString.replaceAll(matcher.group(0), replacement);
        }

        return Parser.getColorizedText(placeholderString);
    }
}
