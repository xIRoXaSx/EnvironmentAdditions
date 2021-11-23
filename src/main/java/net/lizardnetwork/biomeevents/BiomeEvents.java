package net.lizardnetwork.biomeevents;

import net.lizardnetwork.biomeevents.configuration.Config;
import net.lizardnetwork.biomeevents.helper.Parser;
import net.lizardnetwork.biomeevents.models.BiomeModel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class BiomeEvents extends JavaPlugin implements Listener, CommandExecutor {
    private final String pluginName = this.getDescription().getName();
    private static int positionChecksInTicks = -1;
    private Config config = new Config(this);
    private BukkitTask locationCheckerTask;
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

        start();
        Bukkit.getLogger().info("Enabled " + pluginName + " within " + getFormattedTime(startTime));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0)
            return true;

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(pluginName.toLowerCase() + ".admin"))
                return false;

            String elapsedTime = reload();

            if (sender instanceof Player)
                Bukkit.getLogger().info(elapsedTime);

            sender.sendMessage(elapsedTime);

            return true;
        }

        return false;
    }

    /**
     * The start logic of this plugin
     */
    private void start() {
        biomeModels = config.getBiomeConfigs();
        Bukkit.getLogger().info("Found " + biomeModels.size() + " biomes in the config!");
        positionChecksInTicks = Parser.parse(config.getConfigProperty("BiomeEvents.Settings.PositionChecksInTicks"), 20);
        locationCheckerTask = startLocationChecker();
    }

    /**
     * Reload the configuration and restart the BukkitTask
     * @return String - The time taken to reload
     */
    private String reload() {
        long startTime = System.nanoTime();

        locationCheckerTask.cancel();
        config = new Config(this);
        start();

        return Parser.getColorizedText("&aReloaded " + pluginName + "&f within " + getFormattedTime(startTime));
    }

    /**
     * Get the elapsed time formatted in ms or s depending on the length
     * @param startTime long - The start time
     * @return String - Formatted string link '10ms'
     */
    private String getFormattedTime(long startTime) {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        return (elapsed <= 1000 ? elapsed + "ms": elapsed / 1000 + "s");
    }

    /**
     * Start the LocationChecker BukkitTask
     * @return BukkitTask - Running BukkitTask of LocationChecker
     */
    private BukkitTask startLocationChecker() {
        return new LocationChecker(this, config.getConfigProperty("BiomeEvents.Settings.PapiBiomePlaceholder"))
            .initializeTimeDrivenSystem();
    }

    /**
     * Get the current integer for PositionChecksInTicks
     * @return Integer - PositionChecksInTicks
     */
    public static Integer getPositionChecksInTicks() {
        return positionChecksInTicks;
    }

    /**
     * Return the current set BiomeModel list
     * @return List&lt;BiomeModel&gt; - List containing all BiomeModels from the config
     */
    static List<BiomeModel> getBiomeModels() {
        return biomeModels;
    }
}
