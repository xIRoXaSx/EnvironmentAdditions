package net.lizardnetwork.biomeevents;

import net.lizardnetwork.biomeevents.configuration.Config;
import net.lizardnetwork.biomeevents.helper.Parser;
import net.lizardnetwork.biomeevents.models.BiomeModel;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class BiomeEvents extends JavaPlugin implements Listener {
    private final Config config = new Config(this);
    private static int positionChecksInTicks = -1;

    @Override
    public void onEnable() {
        long startTime = System.nanoTime();

        if (config.createConfig()) {
            this.getLogger().info("Created config file. Disabling plugin... " +
                "Modify the config file to your liking and restart the server to test it out!");

            this.getPluginLoader().disablePlugin(this);
            return;
        }

        List<BiomeModel> biomeModels = config.getBiomeConfigs();
        Bukkit.getLogger().info("BiomeModels: " + biomeModels.size());
        positionChecksInTicks = Parser.parse(config.getConfigProperty("BiomeEvents.Settings.PositionChecksInTicks"), 20);
    }

    /**
     * Get the current integer for PositionChecksInTicks
     * @return Integer - PositionChecksInTicks
     */
    public static Integer getPositionChecksInTicks() {
        return positionChecksInTicks;
    }

    /**
     * Get the current Config instance
     * @return Config - The current config
     */
    Config getCurrentConfig() {
        return config;
    }
}
