package net.lizardnetwork.biomeevents.models;

import net.lizardnetwork.biomeevents.helper.Parser;
import org.bukkit.SoundCategory;
import java.util.*;

public class BiomeModel {
    public String BiomeId;
    public BiomeEventModel WhileIn = new BiomeEventModel();
    private final Map<String, Object> valueMap;

    public BiomeModel(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }

    /**
     * Load values from a map into a new BiomeModel instance
     * @return BiomeModel
     */
    public BiomeModel loadBiomeModel() {
        BiomeId = String.valueOf(valueMap.get("BiomeId"));
        WhileIn.Commands.Commands = convertObjectToList(valueMap.getOrDefault("WhileIn.Commands", new ArrayList<>()));
        List<Map<String, Object>> whileInSounds = convertObjectToMap(valueMap.getOrDefault("WhileIn.Sounds", List.of(Collections.emptyMap())));
        SoundModel whileInSound = new SoundModel();

        for (Map<String, Object> soundModelMap : whileInSounds) {
            whileInSound.Chance = Parser.parse(String.valueOf(soundModelMap.get("Chance")), 1);
            whileInSound.Sound = String.valueOf(soundModelMap.get("Sound"));
            whileInSound.Category = String.valueOf(Parser.parse(String.valueOf(soundModelMap.get("Category")), SoundCategory.AMBIENT));
            whileInSound.Volume = Parser.parse(String.valueOf(soundModelMap.get("Volume")), 0.5f);
            whileInSound.Pitch = Parser.parse(String.valueOf(soundModelMap.get("Pitch")), 1f);
            whileInSound.IsServerWide = Parser.parse(String.valueOf(soundModelMap.get("IsServerWide")), false);
            whileInSound.Permission = String.valueOf(soundModelMap.get("Permission"));
            whileInSound.MaxRandomOffset = Parser.parse(String.valueOf(soundModelMap.get("MaxRandomOffset")), -1f);
        }

        WhileIn.Sounds = List.of(whileInSound);
        return this;
    }

    /**
     * Convert object to list
     * @param object Object - The object to convert
     * @return List&lt;?&gt; - List containing unknown types
     */
    @SuppressWarnings("unchecked")
    private List<String> convertObjectToList(Object object) {
        List<String> returnValue = new ArrayList<>();
        if (object.getClass().isArray()) {
            returnValue = Arrays.asList((String[])object);
        } else if (object instanceof Collection) {
            returnValue = new ArrayList<>((Collection<String>)object);
        }
        return returnValue;
    }

    /**
     * Convert object to list of map
     * @param object Object - The object to convert
     * @return List&lt;Map&lt;String, Object&gt;&gt; - List containing map of Strings and Objects
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> convertObjectToMap(Object object) {
        List<Map<String, Object>> returnValue = new ArrayList<>();
        if (object.getClass().isArray()) {
            returnValue = List.of((Map<String, Object>) object);
        } else if (object instanceof Collection) {
            returnValue = new ArrayList<>((Collection<Map<String, Object>>)object);
        }
        return returnValue;
    }
}
