package net.lizardnetwork.biomeevents.configuration;

import net.lizardnetwork.biomeevents.BiomeEvents;
import net.lizardnetwork.biomeevents.helper.Parser;
import net.lizardnetwork.biomeevents.models.BiomeEventModel;
import net.lizardnetwork.biomeevents.models.BiomeModel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    private final String configName = "config.yml";
    private final Plugin plugin;
    private final File configFile;
    private FileConfiguration config;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), configName);
        config = new YamlConfiguration();

        try {
            if (configFile.exists()) {
                config.load(configFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the net.lizardnetwork.wildtreasures.config file
     * @return Boolean, True if config has been created or false if it hasn't
     */
    public boolean createConfig() {
        boolean returnValue = false;

        if (!configFile.exists()) {
            returnValue = true;

            if (Files.exists(plugin.getDataFolder().toPath()) || configFile.getParentFile().mkdirs())
                plugin.saveResource(configName, false);
        }

        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    /**
     * Gets a property from the config (eg: "BiomeEvents.CheckInterval")
     * @param propertyName The property (path) to get
     * @return String, either containing the value of the property or empty ("")
     */
    public String getConfigProperty(String propertyName) {
        String returnValue = "";
        FileConfiguration configFile = plugin.getConfig();

        if (configFile.contains(propertyName)) {
            returnValue = configFile.getString(propertyName);
        }

        return returnValue;
    }

    /**
     * Get the current biome configurations from config file
     * @return BiomeModel[] - Array containing all BiomeModels
     */
    public List<BiomeModel> getBiomeConfigs() {
        List<BiomeModel> returnValue = new ArrayList<>();
        var biomeModels = config.getConfigurationSection("BiomeEvents.Biomes");

        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        if (biomeModels == null)
            return null;

        for (String biomeModelKey : biomeModels.getKeys(false)) {
            ConfigurationSection configurationSection = config.getConfigurationSection("BiomeEvents.Biomes." + biomeModelKey);

            if (configurationSection == null)
                continue;

            Map<String, Object> valueMap = configurationSection.getValues(true);

            // Append biome name / ID
            valueMap.put("BiomeId", biomeModelKey);
            BiomeModel biomeModel = loadBiomeModel(valueMap);
            returnValue.add(biomeModel);
        }

        return returnValue;
    }

    /**
     * Load values from a map into a new BiomeModel instance
     * @param valueMap Map&lt;String, Object&gt; - Map containing all data for a BiomeModel
     * @return BiomeModel
     */
    private BiomeModel loadBiomeModel(Map<String, Object> valueMap) {
        BiomeModel returnValue = new BiomeModel();
        returnValue.OnEnter.Commands.Commands = (String[]) valueMap.getOrDefault("OnLeave.Sounds.Sound", null);
        returnValue.OnLeave.Commands.Commands = (String[]) valueMap.getOrDefault("OnLeave.Sounds.Sound", null);
        returnValue.OnEnter.Sounds.Sound = valueMap.getOrDefault("OnLeave.Sounds.Sound", "").toString();
        returnValue.OnLeave.Sounds.Sound = valueMap.getOrDefault("OnLeave.Sounds.Sound", "").toString();
        returnValue.OnEnter.Sounds.Volume = Parser.parse(valueMap.getOrDefault("OnLeave.Sounds.Sound", 1.0f).toString(), 1.0f);
        returnValue.OnLeave.Sounds.Volume = Parser.parse(valueMap.getOrDefault("OnLeave.Sounds.Sound", 1.0f).toString(), 1.0f);

        return returnValue;
    }
}
