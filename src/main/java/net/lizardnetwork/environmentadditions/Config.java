package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.models.ModelBiomeEvent;
import net.lizardnetwork.environmentadditions.models.ModelCommand;
import net.lizardnetwork.environmentadditions.models.ModelCondition;
import org.bukkit.WeatherType;
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
    ModelBiomeEvent[] linkConfigs() {
        String biomesGroupKey = "Biomes";
        String whileInKey = "WhileIn";
        String conditionKey = "Condition";
        String commandKey = "Commands";
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
            Logging.debug(String.join(",", activeBiomes));

            Object whileIn = be.get(whileInKey);
            Map<?, ?> wi = castToMap(whileIn);
            if (wi == null) {
                continue;
            }
            Object conditionName = wi.get(conditionKey);
            ModelCondition condition = getConditionByName(conditionName);

            List<?> commandsName = (List<?>)wi.get(commandKey);
            ModelCommand[] command = getCommandsByName(commandsName);

            configuredBiomeEvents.add(
                new ModelBiomeEvent(activeBiomes, condition, command, null, null)
            );
        }

        return null;
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
            if (bg == null) {
                return new String[0];
            }
            List<?> linkedBiomes = (List<?>)biomeLookup.get(bg.toString());
            for (Object lb : linkedBiomes) {
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
        ModelCondition condition = new ModelCondition(false, 0, 0, WeatherType.CLEAR,"");
        if (name == null || Parser.isEmpty(name.toString())) {
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
        String weatherString = conditionSection.getString(weatherKey);
        WeatherType weather;
        weather = weatherString == null ? WeatherType.CLEAR : WeatherType.valueOf(weatherString.toUpperCase());
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
        ModelCondition condition = new ModelCondition(false, 0, 0, WeatherType.CLEAR,"");
        ModelCommand commands = new ModelCommand(new String[0], condition, false);
        if (commandGroups == null) {
            return List.of(commands).toArray(new ModelCommand[0]);
        }

        String pickRandomCommandKey = "PickRandomCommand";
        String commandListKey = "Commands";
        String conditionKey = "Condition";
        List<ModelCommand> modelCommandList = new ArrayList<>();
        for (Object commandGroup : commandGroups) {
            if (commandGroup == null || Parser.isEmpty(commandGroup.toString())) {
                continue;
            }
            String commandsKey = "Commands." + commandGroup;
            ConfigurationSection conditionSection = this.commands.getConfigurationSection(commandsKey);
            if (conditionSection == null) {
                Logging.warn("Unable to retrieve command object!");
                continue;
            }

            String conditionName = conditionSection.getString(conditionKey);
            condition = getConditionByName(conditionName);
            List<String> commandList = conditionSection.getStringList(commandListKey);
            String[] commandArray = commandList.toArray(new String[0]);
            modelCommandList.add(new ModelCommand(
                commandArray,
                condition,
                conditionSection.getBoolean(pickRandomCommandKey)
            ));
        }
        return modelCommandList.toArray(new ModelCommand[0]);
    }

    private Map<?,?> castToMap(Object o) {
        if (!(o instanceof Map<?, ?> map)) {
            Logging.warn("Unable to cast object to map");
            return null;
        }
        return map;
    }
}










