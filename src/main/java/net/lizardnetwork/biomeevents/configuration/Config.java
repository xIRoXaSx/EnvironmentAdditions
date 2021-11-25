package net.lizardnetwork.biomeevents.configuration;

import net.lizardnetwork.biomeevents.models.BiomeModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private final String configName = "config.yml";
    private final Plugin plugin;
    private final File configFile;
    private static FileConfiguration config;

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
     * Creates the config file
     * @return <code>Boolean</code> - <code>True</code> if config has been created or <code>false</code> if it hasn't
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
     * Gets a property from the current config
     * @param propertyName <code>String</code> - The property (path) to get
     * @return <code>String</code> - Either containing the value of the property or empty ("")
     */
    public String getConfigProperty(String propertyName) {
        String returnValue = "";

        if (config.contains(propertyName)) {
            returnValue = config.getString(propertyName);
        }

        return returnValue;
    }

    /**
     * Get the current biome configurations from the current config
     * @return <code>List&lt;BiomeModel&gt;</code> - List containing all BiomeModels
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
            BiomeModel biomeModel = new BiomeModel(valueMap).loadBiomeModel();
            returnValue.add(biomeModel);
        }

        return returnValue;
    }

    /**
     * Get the biome sections from the current config
     * @return <code>Map&lt;String</code> - Object&gt; - Map containing BiomeId as key and the biome entries as its value
     */
    public static Map<String, Object> getBiomes() {
        Map<String, Object> returnValue = new HashMap<>();
        ConfigurationSection configSection = config.getConfigurationSection("BiomeEvents.Biomes");

        if (configSection == null)
            return new HashMap<>();

        for (String biomeModelKey : configSection.getKeys(false)) {
            ConfigurationSection biomeConfigSection = config.getConfigurationSection("BiomeEvents.Biomes." + biomeModelKey);

            if (biomeConfigSection == null)
                continue;

            returnValue.put(biomeModelKey, biomeConfigSection.getValues(true));
        }

        return returnValue;
    }
}
