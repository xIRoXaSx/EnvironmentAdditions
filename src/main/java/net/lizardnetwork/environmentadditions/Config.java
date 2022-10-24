package net.lizardnetwork.environmentadditions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Files;


public class Config {
    private final FileConfiguration config;

    public Config() {
        Plugin plugin = EnvironmentAdditions.getInstance();
        this.config = new YamlConfiguration();
        String fileName = "config.yml";
        File file = new File(plugin.getDataFolder(), fileName);
        if (file.exists()) {
            load(file);
            return;
        }

        if (!Files.exists(plugin.getDataFolder().toPath()) && !file.getParentFile().mkdirs()) {
            Exceptions.Fatal("Unable to get / create config, disabling...");
            return;
        }
        plugin.saveResource(fileName, false);
        load(file);
    }

    private void load(File file) {
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
            Exceptions.Fatal("Unable to load config file, disabling...");
        }
    }
}
