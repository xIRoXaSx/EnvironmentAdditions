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
    static List<BiomeModel> biomeModels;

    @Override
    public void onEnable() {
        long startTime = System.nanoTime();

        if (config.createConfig()) {
            this.getLogger().info("Created config file. Disabling plugin... " +
                "Modify the config file to your liking and restart the server to test it out!");

            this.getPluginLoader().disablePlugin(this);
            return;
        }

        biomeModels = config.getBiomeConfigs();

        Bukkit.getLogger().info("Found " + biomeModels.size() + " biomes in the config!");
        positionChecksInTicks = Parser.parse(config.getConfigProperty("BiomeEvents.Settings.PositionChecksInTicks"), 20);
        LocationChecker locationChecker = new LocationChecker(this, config.getConfigProperty("BiomeEvents.Settings.PapiBiomePlaceholder"));
        locationChecker.initializeTimeDrivenSystem();
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

    /**
     * Return the current set BiomeModel list
     * @return List&lt;BiomeModel&gt; - List containing all BiomeModels from the config
     */
    static List<BiomeModel> getBiomeModels() {
        return biomeModels;
    }
}
