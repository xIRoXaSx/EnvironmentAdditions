package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.enums.ECommandExecutor;
import net.lizardnetwork.environmentadditions.enums.EDependency;
import net.lizardnetwork.environmentadditions.enums.ELightSource;
import net.lizardnetwork.environmentadditions.enums.EParticleLoop;
import net.lizardnetwork.environmentadditions.enums.EWeatherCondition;
import net.lizardnetwork.environmentadditions.helper.Caster;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.helper.Random;
import net.lizardnetwork.environmentadditions.helper.Resolve;
import net.lizardnetwork.environmentadditions.models.*;
import org.bukkit.Color;
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
import java.util.*;

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

    ModelSettings getSettings() {
        String rootKey = "EnvironmentAdditions.Settings";
        String[] keys = new String[]{
            rootKey + ".PositionChecksInTicks",
            rootKey + ".PapiBiomePlaceholder",
            rootKey + ".UseSingleGroupMode"
        };
        return new ModelSettings(
            this.config.getInt(keys[0]),
            this.config.getString(keys[1]),
            this.config.getBoolean(keys[2])
        );
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

        boolean wgEnabled = false;
        List<ModelBiomeEvent> configuredBiomeEvents = new ArrayList<>();
        List<?> biomeEvents = config.getList(biomesKey);
        if (biomeEvents == null) {
            return null;
        }
        for (Object biomeEvent : biomeEvents) {
            Map<?, ?> be = Caster.castToMap(biomeEvent);
            if (be == null) {
                continue;
            }

            Object biomeGroupKey = be.get("BiomeGroup");
            String[] activeBiomes = getLinkedBiomesByName(biomeGroupKey);
            Object whileIn = be.get(whileInKey);
            Map<?, ?> wi = Caster.castToMap(whileIn);
            if (wi == null) {
                continue;
            }

            Object conditionName = wi.get(conditionKey);
            ModelCondition condition = getConditionByName(biomesKey + "." + biomeGroupKey, conditionName);
            List<?> commandNames = (List<?>)wi.get(commandKey);
            ModelCommand[] commands = getCommandsByName(commandNames);
            List<?> particleNames = (List<?>)wi.get(particleKey);
            ModelParticle[] particles = getParticlesByName(particleNames);
            List<?> soundNames = (List<?>)wi.get(soundKey);
            ModelSound[] sounds = getSoundsByName(soundNames);
            configuredBiomeEvents.add(
                new ModelBiomeEvent(activeBiomes, condition, commands, particles, sounds)
            );
            wgEnabled |= condition.getWorldGuardCondition().isConfigured();
        }

        int deps = Resolve.resolveDependencies().getValue();
        if (wgEnabled) {
            deps += EDependency.WordGuard.getValue();
        }
        EnvironmentAdditions.getState().setDependencies(deps);
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

    private Map<String, Object> getConfigValues(FileConfiguration config, Object rootKey, String[] subKeys) {
        Map<String, Object> value = new HashMap<>();
        if (Parser.isEmpty(config) || Parser.isEmpty(rootKey)) {
            Logging.warn("Unable to retrieve sub keys, root key / config section was empty!");
            return value;
        }

        String mainKey = rootKey.toString();
        ConfigurationSection configSection = config.getConfigurationSection(mainKey);
        if (configSection == null) {
            Logging.warn("Unable to retrieve config section for " + mainKey + "!");
            return value;
        }

        for (String subKey : subKeys) {
            Object val = configSection.get(subKey);
            if (Parser.isEmpty(val)) {
                continue;
            }
            value.put(subKey, val);
        }
        return value;
    }

    /**
     * Get a configured condition by name.
     * @param requestKey Object - The original key that corresponds to the condition resolution, just for logging.
     * @param name Object - The name of the condition to retrieve.
     * @return ModelCondition - The condition.
     */
    private ModelCondition getConditionByName(Object requestKey, Object name) {
        if (Parser.isEmpty(name)) {
            Logging.warn("Condition object name " + (Parser.isEmpty(requestKey) ? "" : "of " + requestKey + " ") +
                "was empty, fallback: enabling event executing!"
            );
            return ModelCondition.getDefault(true);
        }

        String lightLevelSubKey = "LightLevel";
        String lightLevelLevelSubKey = lightLevelSubKey + ".Level";
        String blockSubKey = "Block";
        String relativeOffsetSubKey = blockSubKey + ".RelativeOffset";
        String areaSubKey = "Area";
        String areaMinSubKey = areaSubKey + ".Minimum";
        String areaMaxSubKey = areaSubKey + ".Maximum";
        String offsetXSubKey = "X";
        String offsetYSubKey = "Y";
        String offsetZSubKey = "Z";
        String worldGuardRegionSubKey = "WorldGuard";
        String dateTimeSubKey = "DateTime";
        String[] subKeys = new String[]{
            "IsEnabled",
            "Chance",
            "FromTimeInTicks",
            "UntilTimeInTicks",
            "Weather",
            "Permission",
            combineKeys(lightLevelSubKey, "Type"),
            combineKeys(lightLevelLevelSubKey, "Minimum"),
            combineKeys(lightLevelLevelSubKey, "Maximum"),
            combineKeys(blockSubKey, "Type"),
            combineKeys(relativeOffsetSubKey, offsetXSubKey),
            combineKeys(relativeOffsetSubKey, offsetYSubKey),
            combineKeys(relativeOffsetSubKey, offsetZSubKey),
            combineKeys(areaSubKey, "CompareExactly"),
            combineKeys(areaMinSubKey, offsetXSubKey),
            combineKeys(areaMinSubKey, offsetYSubKey),
            combineKeys(areaMinSubKey, offsetZSubKey),
            combineKeys(areaMaxSubKey, offsetXSubKey),
            combineKeys(areaMaxSubKey, offsetYSubKey),
            combineKeys(areaMaxSubKey, offsetZSubKey),
            combineKeys(worldGuardRegionSubKey, "UseSingleRegionMode"),
            combineKeys(worldGuardRegionSubKey, "InRegion"),
            combineKeys(worldGuardRegionSubKey, "NotInRegion"),
            combineKeys(dateTimeSubKey, "From"),
            combineKeys(dateTimeSubKey, "To"),
        };
        String rootKey = "Conditions." + name;
        Map<String, Object> configValues = getConfigValues(this.conditions, rootKey, subKeys);
        if (configValues.size() == 0) {
            Logging.warn("Unable to retrieve condition object: " + (Parser.isEmpty(requestKey) ? "" : requestKey + ".") +
                rootKey + ", fallback: disabling event executing!"
            );
            return ModelCondition.getDefault(false);
        }
        Object chance = configValues.get(subKeys[1]);
        Object isEnabled = configValues.get(subKeys[0]);
        return new ModelCondition(
            isEnabled != null ? (boolean)isEnabled : true,
            (int)(chance != null ? chance : -1),
            Caster.castToInt(configValues.get(subKeys[2]), -1),
            Caster.castToInt(configValues.get(subKeys[3]), -1),
            Parser.valueOf(EWeatherCondition.class, configValues.get(subKeys[4])),
            Caster.valueOrEmpty(configValues.get(subKeys[5])),
            new ModelConditionLight(
                Parser.valueOf(ELightSource.class, configValues.get(subKeys[6])),
                Caster.castToInt(configValues.get(subKeys[7]), -1),
                Caster.castToInt(configValues.get(subKeys[8]), -1)
            ),
            new ModelConditionBlock(
                Caster.valueOrEmpty(configValues.get(subKeys[9])),
                new ModelPosOffset(
                    Caster.castToDouble(configValues.get(subKeys[10]), 0),
                    Caster.castToDouble(configValues.get(subKeys[11]), 0),
                    Caster.castToDouble(configValues.get(subKeys[12]), 0)
                )
            ),
            new ModelConditionArea(
                Caster.castToBoolean(configValues.get(subKeys[13]), false),
                new ModelPosOffset(
                    Caster.castToDouble(configValues.get(subKeys[14]), 0),
                    Caster.castToDouble(configValues.get(subKeys[15]), 0),
                    Caster.castToDouble(configValues.get(subKeys[16]), 0)
                ),
                new ModelPosOffset(
                    Caster.castToDouble(configValues.get(subKeys[17]), 0),
                    Caster.castToDouble(configValues.get(subKeys[18]), 0),
                    Caster.castToDouble(configValues.get(subKeys[19]), 0)
                )
            ),
            new ModelConditionWorldGuard(
                Caster.castToBoolean(configValues.get(subKeys[20]), false),
                Caster.castToList(String.class, configValues.get(subKeys[21])).toArray(new String[0]),
                Caster.castToList(String.class, configValues.get(subKeys[22])).toArray(new String[0])
            ),
            new ModelConditionDate(
                Caster.valueOrEmpty(configValues.get(subKeys[23])),
                Caster.valueOrEmpty(configValues.get(subKeys[24]))
            )
        );
    }

    /**
     * Get the configured commands by name.
     * @return ModelCommand - The commands.
     */
    private ModelCommand[] getCommandsByName(List<?> groups) {
        ModelCondition condition = ModelCondition.getDefault(false);
        ModelCommand commands = new ModelCommand(new String[0], ECommandExecutor.PLAYER, condition, false);
        if (groups == null || groups.size() == 0) {
            return List.of(commands).toArray(new ModelCommand[0]);
        }
        if (EnvironmentAdditions.getState().getSettings().isSingleModelMode()) {
            int index = new Random(0, groups.size()).getIntResult();
            groups = List.of(groups.get(index));
        }

        String[] subKeys = new String[]{
            "PickRandomCommand",
            "Commands",
            "Condition",
            "Executor"
        };
        List<ModelCommand> modelList = new ArrayList<>();
        for (Object group : groups) {
            if (Parser.isEmpty(group)) {
                continue;
            }

            String rootKey = "Commands." + group;
            Map<String, Object> configValues = getConfigValues(this.commands, rootKey, subKeys);
            if (configValues.size() == 0) {
                Logging.warn("Unable to retrieve command object: " + rootKey + ", " +
                    "fallback: disabling event executing!"
                );
                return new ModelCommand[]{commands};
            }
            modelList.add(new ModelCommand(
                Caster.castToList(String.class, configValues.get(subKeys[1])).toArray(new String[0]),
                Parser.valueOf(ECommandExecutor.class, configValues.get(subKeys[3])),
                getConditionByName(rootKey, configValues.get(subKeys[2])),
                (boolean)configValues.get(subKeys[0])
            ));
        }
        return modelList.toArray(new ModelCommand[0]);
    }

    /**
     * Get the configured particles by name.
     * @return ModelParticle - The commands.
     */
    private ModelParticle[] getParticlesByName(List<?> groups) {
        ModelCondition condition = ModelCondition.getDefault(false);
        Color color = Parser.hexToColor("ffffff");
        ModelParticle particles = new ModelParticle(Particle.REDSTONE, color, 1, 1, false, condition, null);
        if (groups == null || groups.size() == 0) {
            return List.of(particles).toArray(new ModelParticle[0]);
        }
        if (EnvironmentAdditions.getState().getSettings().isSingleModelMode()) {
            int index = new Random(0, groups.size()).getIntResult();
            groups = List.of(groups.get(index));
        }

        String animationSubKey = "Animation";
        String relativeOffsetSubKey = combineKeys(animationSubKey, "RelativeOffset");
        String loopOptionSubKey = combineKeys(animationSubKey, "LoopOption");
        String[] subKeys = new String[]{
            "Particle",
            "RedstoneHexColor",
            "RedstoneSize",
            "ParticleCount",
            "IsGlobal",
            "Condition",
            combineKeys(animationSubKey, "ViewDirectionDistance"),
            combineKeys(relativeOffsetSubKey, "X"),
            combineKeys(relativeOffsetSubKey, "Y"),
            combineKeys(relativeOffsetSubKey, "Z"),
            combineKeys(loopOptionSubKey, "Type"),
            combineKeys(loopOptionSubKey, "ChanceForEachLoop"),
            combineKeys(loopOptionSubKey, "RadiusInBlocks")
        };
        List<ModelParticle> modelList = new ArrayList<>();
        for (Object group : groups) {
            if (Parser.isEmpty(group)) {
                continue;
            }

            String rootKey = "Particles." + group;
            Map<String, Object> configValues = getConfigValues(this.particles, rootKey, subKeys);
            if (configValues.size() == 0) {
                Logging.warn("Unable to retrieve particle object: " + rootKey + ", " +
                    "fallback: disabling event executing!"
                );
                return new ModelParticle[]{particles};
            }
            modelList.add(new ModelParticle(
                Parser.valueOf(Particle.class, configValues.get(subKeys[0])),
                Parser.hexToColor(configValues.get(subKeys[1])),
                (int)configValues.get(subKeys[2]),
                (int)configValues.get(subKeys[3]),
                Caster.castToBoolean(configValues.get(subKeys[4]), false),
                getConditionByName(rootKey, configValues.get(subKeys[5])),
                new ModelParticleAnimation(
                    Caster.castToFloat(configValues.get(subKeys[6])),
                    new ModelPosOffset(
                        Caster.castToDouble(configValues.get(subKeys[7]), 0),
                        Caster.castToDouble(configValues.get(subKeys[8]), 0),
                        Caster.castToDouble(configValues.get(subKeys[9]), 0)
                    ),
                    new ModelParticleLoop(
                        Parser.valueOf(EParticleLoop.class, configValues.get(subKeys[10])),
                        (int)configValues.get(subKeys[11]),
                        (int)configValues.get(subKeys[12])
                    )
                )
            ));
        }
        return modelList.toArray(new ModelParticle[0]);
    }

    /**
     * Get the configured particles by name.
     * @return ModelParticle - The commands.
     */
    private ModelSound[] getSoundsByName(List<?> groups) {
        ModelCondition condition = ModelCondition.getDefault(false);
        ModelSound sound = new ModelSound(Sound.WEATHER_RAIN.getKey().toString(), SoundCategory.MUSIC, 0, 0, false, 0, condition);
        if (groups == null || groups.size() == 0) {
            return List.of(sound).toArray(new ModelSound[0]);
        }
        if (EnvironmentAdditions.getState().getSettings().isSingleModelMode()) {
            int index = new Random(0, groups.size()).getIntResult();
            groups = List.of(groups.get(index));
        }

        String[] subKeys = new String[]{
            "Sound",
            "Category",
            "Volume",
            "Pitch",
            "IsGlobal",
            "MaxRandomOffset",
            "Condition"
        };
        List<ModelSound> modelList = new ArrayList<>();
        for (Object group : groups) {
            if (Parser.isEmpty(group)) {
                continue;
            }

            String rootKey = "Sounds." + group;
            Map<String, Object> configValues = getConfigValues(this.sounds, rootKey, subKeys);
            if (configValues.size() == 0) {
                Logging.warn("Unable to retrieve sound object: " + rootKey + ", " +
                    "fallback: disabling event executing!"
                );
                return new ModelSound[]{sound};
            }
            modelList.add(new ModelSound(
                Caster.valueOrEmpty(configValues.get(subKeys[0])),
                Parser.valueOf(SoundCategory.class, configValues.get(subKeys[1])),
                Caster.castToFloat(configValues.get(subKeys[2])),
                Caster.castToFloat(configValues.get(subKeys[3])),
                (boolean)configValues.get(subKeys[4]),
                Caster.castToFloat(configValues.get(subKeys[5])),
                getConditionByName(rootKey, configValues.get(subKeys[6]))
            ));
        }
        return modelList.toArray(new ModelSound[0]);
    }

    private String combineKeys(String... keys) {
        return String.join(".", keys);
    }
}
