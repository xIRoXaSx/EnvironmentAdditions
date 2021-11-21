package net.lizardnetwork.biomeevents.configuration;

import net.lizardnetwork.biomeevents.BiomeEvents;
import net.lizardnetwork.biomeevents.models.BiomeEventModel;
import net.lizardnetwork.biomeevents.models.BiomeModel;
import net.lizardnetwork.biomeevents.models.CommandModel;
import net.lizardnetwork.biomeevents.models.SoundModel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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
        List<BiomeModel> biomeModels = (List<BiomeModel>) config.getList("BiomeEvents.Biomes");

        if (biomeModels == null)
            return null;

        return returnValue;
    }
}
