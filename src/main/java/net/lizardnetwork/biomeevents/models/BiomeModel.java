package net.lizardnetwork.biomeevents.models;

import net.lizardnetwork.biomeevents.Logging;
import net.lizardnetwork.biomeevents.configuration.Config;
import net.lizardnetwork.biomeevents.helper.Parser;
import org.bukkit.SoundCategory;
import org.bukkit.WeatherType;
import org.bukkit.configuration.MemorySection;
import java.util.*;

public class BiomeModel {
    public String BiomeId;
    public BiomeEventModel WhileIn = new BiomeEventModel();
    private final Map<String, Object> valueMap;

    /**
     * Create an instance which represents a biome in the configuration
     * @param valueMap <code>Map&lt;String, Object&gt;</code> - Map containing all data from a biome in the config
     */
    public BiomeModel(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }

    /**
     * Load values from a map into a new BiomeModel instance
     * @return <code>BiomeModel</code> - The loaded BiomeModel
     */
    public BiomeModel loadBiomeModel() {
        BiomeId = String.valueOf(valueMap.get("BiomeId"));
        WhileIn.Sounds = new ArrayList<>();
        WhileIn.Commands.Commands = convertObjectToList(valueMap.getOrDefault("WhileIn.Commands", new ArrayList<>()));
        WhileIn.Conditions = convertObjectToConditionModel(valueMap.getOrDefault("WhileIn.Conditions", null));

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
            whileInSound.Conditions = convertObjectToConditionModel(soundModelMap.get("Conditions"));
            WhileIn.Sounds.add(whileInSound);
        }

        return this;
    }

    /**
     * Convert an <code>WhileIn.Conditions</code> object from <code>valueMap</code> to a <code>ConditionModel</code>
     * @return <code>ConditionModel</code> - A ConditionModel which represents conditions for a WhileIn block of a BiomeModel
     */
    @SuppressWarnings("unchecked")
    private ConditionModel convertObjectToConditionModel(Object conditions) {
        ConditionModel returnValue = new ConditionModel();

        if (conditions == null)
            return null;

        Map<String, Object> conditionsMap;

        if (conditions.getClass().equals(LinkedHashMap.class)) {
            conditionsMap = (Map<String, Object>) conditions;
        } else {
            try {
                conditionsMap = ((MemorySection) conditions).getValues(false);
            } catch (ClassCastException ex) {
                Logging.warning("ERROR: " + ex);
                return returnValue;
            }
        }

        returnValue.EnableCondition = Parser.parse(String.valueOf(conditionsMap.get("EnableCondition")), false);
        returnValue.Weather = Parser.parse(String.valueOf(conditionsMap.get("Weather")), WeatherType.CLEAR);
        returnValue.FromTimeInTicks = Parser.parse(String.valueOf(conditionsMap.get("FromTimeInTicks")), 1000);
        returnValue.UntilTimeInTicks = Parser.parse(String.valueOf(conditionsMap.get("UntilTimeInTicks")), 13000);

        return returnValue;
    }

    /**
     * Gets the SoundModel via reference inside a Map
     * @param soundModelMap <code>Map&lt;String, Object&gt;</code> - Map containing all information of one SoundModel block
     * @return <code>SoundModel</code> - Either a new <code>SoundModel</code> with all data configured, or <code>null</code>
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
     * @param object <code>Object</code> - The object to convert
     * @return <code>List&lt;?&gt;</code> - List containing unknown types
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
     * @param object <code>Object</code> - The object to convert
     * @return <code>List&lt;Map&lt;String, Object&gt;&gt;</code> - List containing map of Strings and Objects
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
