package net.lizardnetwork.environmentadditions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.nio.file.Files;
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
}
