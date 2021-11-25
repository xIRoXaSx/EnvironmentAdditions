package net.lizardnetwork.biomeevents.external;

import net.lizardnetwork.biomeevents.helper.Parser;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {
    private final Map<String, String> replacements;
    private String placeholderString;

    public Placeholder(String placeholderString, Map<String, String> replacements) {
        this.placeholderString = placeholderString;
        this.replacements = replacements;
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
}
