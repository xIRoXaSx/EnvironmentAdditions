package net.lizardnetwork.biomeevents.models;

import net.lizardnetwork.biomeevents.Logging;
import net.lizardnetwork.biomeevents.configuration.Config;
import net.lizardnetwork.biomeevents.helper.Parser;
import org.bukkit.SoundCategory;
import org.bukkit.WeatherType;
import org.bukkit.configuration.MemorySection;
import java.util.*;

public class BiomeModel {
    private String biomeId;
    private final BiomeEventModel whileIn = new BiomeEventModel();
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
        biomeId = String.valueOf(valueMap.get("BiomeId"));
        whileIn.Sounds = new ArrayList<>();
        whileIn.Commands.Commands = convertObjectToList(valueMap.getOrDefault("WhileIn.Commands", new ArrayList<>()));
        whileIn.Conditions = convertObjectToConditionModel(valueMap.getOrDefault("WhileIn.Conditions", null));
        whileIn.ParticleModels = new ArrayList<>();

        List<Map<String, Object>> whileInSounds = convertObjectToMap(valueMap.getOrDefault("WhileIn.Sounds", List.of(Collections.emptyMap())));
        List<Map<String, Object>> whileInParticles = convertObjectToMap(valueMap.getOrDefault("WhileIn.Particles", List.of(Collections.emptyMap())));

        for (Map<String, Object> soundModelMap : whileInSounds) {
            // Check if the map contains a reference to another biome
            List<SoundModel> soundReferences = getSoundModelFromReference(soundModelMap);

            if (soundReferences != null) {
                whileIn.Sounds.addAll(soundReferences);
                continue;
            }

            SoundModel whileInSound = new SoundModel();
            whileInSound.setChance(Parser.parse(String.valueOf(soundModelMap.get("Chance")), 1));
            whileInSound.setSound(String.valueOf(soundModelMap.get("Sound")));
            whileInSound.setCategory(String.valueOf(Parser.parse(String.valueOf(soundModelMap.get("Category")), SoundCategory.AMBIENT)));
            whileInSound.setVolume(Parser.parse(String.valueOf(soundModelMap.get("Volume")), 0.5f));
            whileInSound.setPitch(Parser.parse(String.valueOf(soundModelMap.get("Pitch")), 1f));
            whileInSound.setIsServerWide(Parser.parse(String.valueOf(soundModelMap.get("IsServerWide")), false));
            whileInSound.setMaxRandomOffset(Parser.parse(String.valueOf(soundModelMap.get("MaxRandomOffset")), -1f));
            whileInSound.setConditions(convertObjectToConditionModel(soundModelMap.get("Conditions")));
            whileIn.Sounds.add(whileInSound);
        }

        for (Map<String, Object> particleModelMap : whileInParticles) {
            ParticleModel particleModel = new ParticleModel();
            particleModel.setParticle(String.valueOf(particleModelMap.get("Particle")));
            particleModel.setRedstoneHexColor(String.valueOf(particleModelMap.get("RedstoneHexColor")));
            particleModel.setRedstoneSize(Parser.parse(String.valueOf(particleModelMap.get("RedstoneSize")), 1));
            particleModel.setParticleCount(Parser.parse(String.valueOf(particleModelMap.get("ParticleCount")), 1));
            particleModel.setConditions(convertObjectToConditionModel(particleModelMap.get("Conditions")));
            particleModel.setParticleAnimationModel(convertObjectToAnimationModel(particleModelMap.get("Animation")));
            whileIn.ParticleModels.add(particleModel);
        }

        return this;
    }

    /**
     * Get the BiomeId
     * @return <code>String</code> - String containing the biome name
     */
    public String getBiomeId() {
        return biomeId;
    }

    /**
     * Get the BiomeId
     * @return <code>BiomeEventModel</code> - The BiomeEventModel which contains all operations
     */
    public BiomeEventModel getWhileInBiomeEventModel() {
        return whileIn;
    }

    /**
     * Convert an <code>WhileIn.Conditions</code> object from <code>valueMap</code> to a <code>ConditionModel</code>
     * @param conditions <code>Object</code> - The object to convert
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

        returnValue.setIsEnabled(Parser.parse(String.valueOf(conditionsMap.get("EnableCondition")), false));
        returnValue.setWeather(Parser.parse(String.valueOf(conditionsMap.get("Weather")), WeatherType.CLEAR));
        returnValue.setFromTimeInTicks(Parser.parse(String.valueOf(conditionsMap.get("FromTimeInTicks")), 1000));
        returnValue.setUntilTimeInTicks(Parser.parse(String.valueOf(conditionsMap.get("UntilTimeInTicks")), 13000));
        returnValue.setPermission(String.valueOf(conditionsMap.get("Permission")));

        return returnValue;
    }

    /**
     * Convert an <code>WhileIn.Particles</code> object from <code>valueMap</code> to a <code>ParticleAnimationModel</code>
     * @param animationModel <code>Object</code> - The object to convert
     * @return <code>ParticleAnimationModel</code> - A ParticleAnimationModel which represents particle animation information
     */
    @SuppressWarnings("unchecked")
    private ParticleAnimationModel convertObjectToAnimationModel(Object animationModel) {
        ParticleAnimationModel returnValue = new ParticleAnimationModel();

        if (animationModel == null)
            return null;

        Map<String, Object> animationMap;

        if (animationModel.getClass().equals(LinkedHashMap.class)) {
            animationMap = (Map<String, Object>) animationModel;
        } else {
            try {
                animationMap = ((MemorySection) animationModel).getValues(false);
            } catch (ClassCastException ex) {
                Logging.warning("ERROR: " + ex);
                return returnValue;
            }
        }

        returnValue.setViewDirectionDistance(Parser.parse(String.valueOf(animationMap.get("ViewDirectionDistance")), 0f));
        returnValue.setRelativeOffsetX(Parser.parse(String.valueOf(animationMap.get("RelativeOffsetX")), 0f));
        returnValue.setRelativeOffsetY(Parser.parse(String.valueOf(animationMap.get("RelativeOffsetY")), 5f));
        returnValue.setRelativeOffsetZ(Parser.parse(String.valueOf(animationMap.get("RelativeOffsetZ")), 0f));
        returnValue.setLoopOption(convertObjectToParticleLoopOptionModel(animationMap.get("LoopOption")));
        return returnValue;
    }

    /**
     * Convert an <code>WhileIn.Particles.Animation.LoopObject</code> object from <code>valueMap</code> to a <code>ParticleLoopOptionModel</code>
     * @param loopOptionModel <code>Object</code> - The object to convert
     * @return <code>ParticleLoopOptionModel</code> - A ParticleLoopOptionModel which represents particle animation loop option information
     */
    @SuppressWarnings("unchecked")
    private ParticleLoopOptionModel convertObjectToParticleLoopOptionModel(Object loopOptionModel) {
        ParticleLoopOptionModel returnValue = new ParticleLoopOptionModel();

        if (loopOptionModel == null)
            return null;

        Map<String, Object> loopOptionMap;

        if (loopOptionModel.getClass().equals(LinkedHashMap.class)) {
            loopOptionMap = (Map<String, Object>) loopOptionModel;
        } else {
            try {
                loopOptionMap = ((MemorySection) loopOptionModel).getValues(false);
            } catch (ClassCastException ex) {
                Logging.warning("ERROR: " + ex);
                return returnValue;
            }
        }

        returnValue.setVersion(Parser.parse(String.valueOf(loopOptionMap.get("Version")), 0));
        returnValue.setChanceForEachLoop(Parser.parse(String.valueOf(loopOptionMap.get("ChanceForEachLoop")), 1));
        returnValue.setRadiusInBlocks(Parser.parse(String.valueOf(loopOptionMap.get("RadiusInBlocks")), 10));
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

            return biomeModel.whileIn.Sounds;
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
