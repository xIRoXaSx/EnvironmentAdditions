package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.enums.CommandExecutor;
import net.lizardnetwork.environmentadditions.enums.ParticleLoop;
import net.lizardnetwork.environmentadditions.enums.WeatherCondition;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.models.*;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private final FileConfiguration config = new YamlConfiguration();
    private final FileConfiguration biomes = new YamlConfiguration();
    private final FileConfiguration commands = new YamlConfiguration();
    private final FileConfiguration conditions = new YamlConfiguration();
    private final FileConfiguration particles = new YamlConfiguration();
    private final FileConfiguration sounds = new YamlConfiguration();

    public Config() {
        Plugin plugin = EnvironmentAdditions.getInstance();
        if (!Files.exists(plugin.getDataFolder().toPath()) && !plugin.getDataFolder().mkdirs()) {
            Exceptions.Fatal("Unable to get / create config, disabling...");
            return;
        }

        Map<String, FileConfiguration> files = Map.of(
            "config", this.config,
            "biomes", this.biomes,
            "commands", this.commands,
            "conditions", this.conditions,
            "particles", this.particles,
            "sounds", this.sounds
        );
        for (Map.Entry<String, FileConfiguration> file : files.entrySet()) {
            createOrLoad(plugin, file);
        }
    }

    private void createOrLoad(Plugin plugin, Map.Entry<String, FileConfiguration> config) {
        String configExtension = ".yml";
        File f = new File(plugin.getDataFolder(), config.getKey()+configExtension);
        if (f.exists()) {
            load(f, config.getValue());
            return;
        }
        plugin.saveResource(config.getKey()+configExtension, false);
        load(f, config.getValue());
    }

    private void load(File file, FileConfiguration config) {
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
            Exceptions.Fatal("Unable to load config file, disabling...");
        }
    }

    @Nullable
    ModelBiomeEvent[] getLinkedConfigs() {
        String biomesGroupKey = "Biomes";
        String whileInKey = "WhileIn";
        String conditionKey = "Condition";
        String commandKey = "Commands";
        String particleKey = "Particles";
        String soundKey = "Sounds";
        String biomesKey = "EnvironmentAdditions." + biomesGroupKey;
        ConfigurationSection biomeGroups = this.biomes.getConfigurationSection(biomesGroupKey);
        if (biomeGroups == null) {
            Logging.warn("Unable to retrieve biome groups!");
            return null;
        }

        List<ModelBiomeEvent> configuredBiomeEvents = new ArrayList<>();
        List<?> biomeEvents = config.getList(biomesKey);
        if (biomeEvents == null) {
            return null;
        }
        for (Object biomeEvent : biomeEvents) {
            Map<?, ?> be = castToMap(biomeEvent);
            if (be == null) {
                continue;
            }

            String[] activeBiomes = getLinkedBiomesByName(be.get("BiomeGroup"));
            Object whileIn = be.get(whileInKey);
            Map<?, ?> wi = castToMap(whileIn);
            if (wi == null) {
                continue;
            }

            Object conditionName = wi.get(conditionKey);
            ModelCondition condition = getConditionByName(conditionName);
            List<?> commandNames = (List<?>)wi.get(commandKey);
            ModelCommand[] commands = getCommandsByName(commandNames);
            List<?> particleNames = (List<?>)wi.get(particleKey);
            ModelParticle[] particles = getParticlesByName(particleNames);
            List<?> soundNames = (List<?>)wi.get(soundKey);
            ModelSound[] sounds = getSoundsByName(soundNames);

            configuredBiomeEvents.add(
                new ModelBiomeEvent(activeBiomes, condition, commands, particles, sounds)
            );
        }
        return configuredBiomeEvents.toArray(new ModelBiomeEvent[0]);
    }

    /**
     * Same as biomeLookup() but with a single biome group.
     * @return String[] - Array of biome names.
     */
    private String[] getLinkedBiomesByName(Object biomeGroup) {
        return biomeLookup(List.of(biomeGroup));
    }

    /**
     * Get every biome of all stated groups.
     * Each given Object in biomeGroups will be converted to a String while iterating over them.
     * @return String[] - Array of biome names.
     */
    private String[] biomeLookup(List<Object> biomeGroups) {
        String biomesGroupKey = "Biomes";
        ConfigurationSection groups = this.biomes.getConfigurationSection(biomesGroupKey);
        if (groups == null) {
            Logging.warn("Unable to retrieve biome groups!");
            return new String[0];
        }
        Map<String, Object> biomeLookup = groups.getValues(false);

        // Create a list to append the entries.
        // Since this is not a hot path, we can use Lists and convert them later.
        List<String> biomesForEvents = new ArrayList<>();
        for (Object bg : biomeGroups) {
            if (Parser.isEmpty(bg)) {
                continue;
            }
            List<?> linkedBiomes = (List<?>)biomeLookup.get(bg.toString());
            for (Object lb : linkedBiomes) {
                if (Parser.isEmpty(lb)) {
                    continue;
                }
                biomesForEvents.add(lb.toString());
            }
        }

        // Convert for efficiency / performance.
        Object[] activeBiomeObjects = new String[biomesForEvents.size()];
        biomesForEvents.toArray(activeBiomeObjects);
        return (String[])activeBiomeObjects;
    }

    /**
     * Get a configured condition by name.
     * @return ModelCondition - The condition.
     */
    private ModelCondition getConditionByName(Object name) {
        ModelCondition condition = ModelCondition.getDefault();
        if (Parser.isEmpty(name)) {
            return condition;
        }

        String conditionsKey = "Conditions." + name;
        String isEnabledKey = "IsEnabled";
        String fromTimeInTicksKey = "FromTimeInTicks";
        String untilTimeInTicksKey = "UntilTimeInTicks";
        String weatherKey = "Weather";
        String permissionKey = "Permission";
        ConfigurationSection conditionSection = this.conditions.getConfigurationSection(conditionsKey);
        if (conditionSection == null) {
            Logging.warn("Unable to retrieve condition object!");
            return condition;
        }
        //String weatherString = conditionSection.getString(weatherKey);
        //weather = weatherString == null ? WeatherType.CLEAR : WeatherType.valueOf(weatherString.toUpperCase());
        WeatherCondition weather = Parser.valueOf(WeatherCondition.class, conditionSection.getString(weatherKey));
        return new ModelCondition(
            conditionSection.getBoolean(isEnabledKey),
            conditionSection.getInt(fromTimeInTicksKey),
            conditionSection.getInt(untilTimeInTicksKey),
            weather,
            conditionSection.getString(permissionKey)
        );
    }

    /**
     * Get the configured commands by name.
     * @return ModelCommand - The commands.
     */
    private ModelCommand[] getCommandsByName(List<?> commandGroups) {
        ModelCondition condition = ModelCondition.getDefault();
        ModelCommand commands = new ModelCommand(new String[0], CommandExecutor.PLAYER, condition, false);
        if (commandGroups == null) {
            return List.of(commands).toArray(new ModelCommand[0]);
        }

        String pickRandomCommandKey = "PickRandomCommand";
        String commandListKey = "Commands";
        String conditionKey = "Condition";
        String executorKey = "Executor";
        List<ModelCommand> modelCommandList = new ArrayList<>();
        for (Object commandGroup : commandGroups) {
            if (Parser.isEmpty(commandGroup)) {
                continue;
            }
            String commandsKey = "Commands." + commandGroup;
            ConfigurationSection commandSection = this.commands.getConfigurationSection(commandsKey);
            if (commandSection == null) {
                Logging.warn("Unable to retrieve command object!");
                continue;
            }

            String conditionName = commandSection.getString(conditionKey);
            condition = getConditionByName(conditionName);
            String executorName = commandSection.getString(executorKey);
            CommandExecutor executor = Parser.valueOf(CommandExecutor.class, executorName);
            List<String> commandList = commandSection.getStringList(commandListKey);
            String[] commandArray = commandList.toArray(new String[0]);
            modelCommandList.add(new ModelCommand(
                commandArray,
                executor,
                condition,
                commandSection.getBoolean(pickRandomCommandKey)
            ));
        }
        return modelCommandList.toArray(new ModelCommand[0]);
    }

    /**
     * Get the configured particles by name.
     * @return ModelParticle - The commands.
     */
    private ModelParticle[] getParticlesByName(List<?> particleGroups) {
        ModelCondition condition = ModelCondition.getDefault();
        ModelParticle particles = new ModelParticle(Particle.REDSTONE, "fff", 1, 1, condition, null);
        if (particleGroups == null) {
            return List.of(particles).toArray(new ModelParticle[0]);
        }

        String particleKey = "Particle";
        String redstoneHexColorKey = "RedstoneHexColor";
        String redstoneSizeKey = "RedstoneSize";
        String particleCountKey = "ParticleCount";
        String conditionKey = "Condition";
        String animationKey = "Animation";
        String viewDirectionDistanceKey = animationKey + ".ViewDirectionDistance";
        String relativeOffsetXKey = animationKey + ".RelativeOffsetX";
        String relativeOffsetYKey = animationKey + ".RelativeOffsetY";
        String relativeOffsetZKey = animationKey + ".RelativeOffsetZ";
        String loopOptionKey = animationKey + ".LoopOption";
        String versionKey = loopOptionKey + ".Version";
        String radiusInBlocksKey = animationKey + "." + loopOptionKey + ".RadiusInBlocks";
        String chanceForEachLoopKey = animationKey + "." + loopOptionKey + ".ChanceForEachLoop";
        List<ModelParticle> modelParticleList = new ArrayList<>();
        for (Object particleGroup : particleGroups) {
            if (Parser.isEmpty(particleGroup)) {
                continue;
            }
            String particlesKey = "Particles." + particleGroup;
            ConfigurationSection particleSection = this.particles.getConfigurationSection(particlesKey);
            if (particleSection == null) {
                Logging.warn("Unable to retrieve particle object!");
                continue;
            }

            String conditionName = particleSection.getString(conditionKey);
            condition = getConditionByName(conditionName);
            String particleName = particleSection.getString(particleKey);
            Particle particle = Parser.valueOf(Particle.class, particleName);
            String hex = particleSection.getString(redstoneHexColorKey);
            int size = particleSection.getInt(redstoneSizeKey);
            int num = particleSection.getInt(particleCountKey);

            // Animation values.
            int viewDistance = particleSection.getInt(viewDirectionDistanceKey);
            int relX = particleSection.getInt(relativeOffsetXKey);
            int relY = particleSection.getInt(relativeOffsetYKey);
            int relZ = particleSection.getInt(relativeOffsetZKey);

            // Loop option values.
            String versionName = particleSection.getString(versionKey);
            ParticleLoop version = Parser.valueOf(ParticleLoop.class, versionName);
            int rad = particleSection.getInt(radiusInBlocksKey);
            int chance = particleSection.getInt(chanceForEachLoopKey);

            modelParticleList.add(new ModelParticle(
                particle,
                hex,
                size,
                num,
                condition,
                new ModelParticleAnimation(
                    viewDistance,
                    relX,
                    relY,
                    relZ,
                    new ModelParticleLoop(version, chance, rad)
                )
            ));
        }
        return modelParticleList.toArray(new ModelParticle[0]);
    }

    /**
     * Get the configured particles by name.
     * @return ModelParticle - The commands.
     */
    private ModelSound[] getSoundsByName(List<?> soundGroups) {
        ModelCondition condition = ModelCondition.getDefault();
        ModelSound sounds = new ModelSound(0, Sound.WEATHER_RAIN.getKey().toString(), SoundCategory.MUSIC, 0, 0, false, 0, condition);
        if (soundGroups == null) {
            return List.of(sounds).toArray(new ModelSound[0]);
        }

        String chanceKey = "Chance";
        String soundKey = "Sound";
        String categoryKey = "Category";
        String volumeKey = "Volume";
        String pitchKey = "Pitch";
        String isGlobalKey = "IsGlobal";
        String maxRandomOffsetKey = "MaxRandomOffset";
        String conditionKey = "Condition";
        List<ModelSound> modelSoundList = new ArrayList<>();
        for (Object soundGroup : soundGroups) {
            if (Parser.isEmpty(soundGroup)) {
                continue;
            }
            String soundsKey = "Sounds." + soundGroup;
            ConfigurationSection soundSection = this.sounds.getConfigurationSection(soundsKey);
            if (soundSection == null) {
                Logging.warn("Unable to retrieve sound object!");
                continue;
            }

            String conditionName = soundSection.getString(conditionKey);
            condition = getConditionByName(conditionName);
            int chance = soundSection.getInt(chanceKey);
            String sound = soundSection.getString(soundKey);
            String categoryName = soundSection.getString(categoryKey);
            SoundCategory category = Parser.valueOf(SoundCategory.class, categoryName);
            float vol = (float)soundSection.getDouble(volumeKey);
            float pitch = (float)soundSection.getDouble(pitchKey);
            boolean isGlobal = soundSection.getBoolean(isGlobalKey);
            int maxRandOff = soundSection.getInt(maxRandomOffsetKey);

            modelSoundList.add(new ModelSound(
                chance,
                sound,
                category,
                vol,
                pitch,
                isGlobal,
                maxRandOff,
                condition
            ));
        }
        return modelSoundList.toArray(new ModelSound[0]);
    }

    private Map<?,?> castToMap(Object o) {
        if (!(o instanceof Map<?, ?> map)) {
            Logging.warn("Unable to cast object to map");
            return null;
        }
        return map;
    }
}