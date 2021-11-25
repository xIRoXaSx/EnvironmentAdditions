package net.lizardnetwork.biomeevents.models;

import net.lizardnetwork.biomeevents.Logging;
import net.lizardnetwork.biomeevents.configuration.Config;
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
        WhileIn.Sounds = new ArrayList<>();

        List<Map<String, Object>> whileInSounds = convertObjectToMap(valueMap.getOrDefault("WhileIn.Sounds", List.of(Collections.emptyMap())));
        for (Map<String, Object> soundModelMap : whileInSounds) {
            // Check if the map contains a reference to another biome
            List<SoundModel> soundReferences = getSoundModelFromReference(soundModelMap);

            if (soundReferences != null) {
                WhileIn.Sounds.addAll(soundReferences);
                continue;
            }

            SoundModel whileInSound = new SoundModel();
            whileInSound.Chance = Parser.parse(String.valueOf(soundModelMap.get("Chance")), 1);
            whileInSound.Sound = String.valueOf(soundModelMap.get("Sound"));
            whileInSound.Category = String.valueOf(Parser.parse(String.valueOf(soundModelMap.get("Category")), SoundCategory.AMBIENT));
            whileInSound.Volume = Parser.parse(String.valueOf(soundModelMap.get("Volume")), 0.5f);
            whileInSound.Pitch = Parser.parse(String.valueOf(soundModelMap.get("Pitch")), 1f);
            whileInSound.IsServerWide = Parser.parse(String.valueOf(soundModelMap.get("IsServerWide")), false);
            whileInSound.Permission = String.valueOf(soundModelMap.get("Permission"));
            whileInSound.MaxRandomOffset = Parser.parse(String.valueOf(soundModelMap.get("MaxRandomOffset")), -1f);
            WhileIn.Sounds.add(whileInSound);
        }

        return this;
    }

    /**
     * Gets the SoundModel via reference inside a Map
     * @param soundModelMap Map&lt;String, Object&gt; - Map containing all information of one SoundModel block
     * @return SoundModel - Either a new SoundModel with all data configured, or null
     */
    private List<SoundModel> getSoundModelFromReference(Map<String, Object> soundModelMap) {
        var referencedEntry = soundModelMap.entrySet().stream()
            .filter(x -> x.getKey().equalsIgnoreCase("reference") || x.getKey().equalsIgnoreCase("ref"))
            .findFirst().orElse(null);

        if (referencedEntry != null) {
            Map<String, Object> biomeConfigs = Config.getBiomes();

            Map.Entry<String, Object> foundValue = biomeConfigs.entrySet().stream()
                .filter(x -> x.getKey().equalsIgnoreCase(referencedEntry.getValue().toString()))
                .findFirst().orElse(null);

            if (foundValue == null)
                return null;

            var biomeModel = new BiomeModel(convertObjectToMap(List.of(foundValue.getValue())).get(0)).loadBiomeModel();

            return biomeModel.WhileIn.Sounds;
        }

        return null;
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
