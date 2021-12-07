package net.lizardnetwork.biomeevents;

import net.lizardnetwork.biomeevents.configuration.Config;
import net.lizardnetwork.biomeevents.helper.Parser;
import net.lizardnetwork.biomeevents.models.BiomeModel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

public class BiomeEvents extends JavaPlugin implements Listener, CommandExecutor {
    private static BiomeEvents instance;
    private static String consolePrefix;
    private static int positionChecksInTicks = -1;
    private static int positionParticleChecksInTicks = -1;
    private static boolean isSingleCommandModelEnabled = true;
    private Config config = new Config(this);
    private BukkitTask locationCheckerTask;
    private BukkitTask locationParticleCheckerTask;
    static List<BiomeModel> biomeModels;

    @Override
    public void onEnable() {
        instance = this;
        consolePrefix = Parser.getGradientText(getInstance().getName(), "#5ee68b");
        long startTime = System.nanoTime();

        if (config.createConfig()) {
            Logging.info("Created config file. Disabling plugin... " +
                "Modify the config file to your liking and restart the server to test it out!");

            this.getPluginLoader().disablePlugin(this);
            return;
        }

        start();
        Logging.info("Enabled " + instance.getDescription().getName() + " within " + getFormattedTime(startTime));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0)
            return true;

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(instance.getDescription().getName().toLowerCase() + ".admin"))
                return false;

            String returnText = "&aReloaded &rwithin " + reload();

            if (sender instanceof Player)
                sender.sendMessage(Parser.getColorizedText(consolePrefix + "&8» " + returnText));

            Logging.info(Parser.getColorizedText(returnText));

            return true;
        }

        return false;
    }

    /**
     * The start logic of this plugin
     */
    private void start() {
        biomeModels = config.getBiomeConfigs();
        Logging.info("Found " + biomeModels.size() + " biomes in the config!");
        positionChecksInTicks = Parser.parse(config.getConfigProperty("BiomeEvents.Settings.PositionChecksInTicks"), 20);
        positionParticleChecksInTicks = Parser.parse(config.getConfigProperty("BiomeEvents.Settings.PositionParticleChecksInTicks"), 20);
        isSingleCommandModelEnabled = Parser.parse(config.getConfigProperty("BiomeEvents.Settings.ExecuteSingleCommandModel"), true);
        locationCheckerTask = startLocationChecker();

        if (positionParticleChecksInTicks > -1)
            locationParticleCheckerTask = startLocationParticleChecker();
    }

    /**
     * Reload the configuration and restart the BukkitTask
     * @return <code>String</code> - The time taken to reload
     */
    private String reload() {
        long startTime = System.nanoTime();

        if (locationParticleCheckerTask != null)
            locationParticleCheckerTask.cancel();

        locationCheckerTask.cancel();
        config = new Config(this);
        start();

        return getFormattedTime(startTime);
    }

    /**
     * Get the elapsed time formatted in ms or s depending on the length
     * @param startTime <code>long</code> - The start time
     * @return <code>String</code> - Formatted string like '10ms'
     */
    private String getFormattedTime(long startTime) {
        long elapsed = (System.nanoTime() - startTime) / 1000000;
        return (elapsed <= 1000 ? elapsed + "ms": elapsed / 1000 + "s");
    }

    /**
     * Start the LocationChecker BukkitTask
     * @return <code>BukkitTask</code> - Start the BukkitTask of LocationChecker
     */
    private BukkitTask startLocationChecker() {
        return new LocationChecker(this, config.getConfigProperty("BiomeEvents.Settings.PapiBiomePlaceholder"))
            .initializeMainTimeDrivenSystem();
    }

    /**
     * Start the LocationParticleChecker BukkitTask
     * @return <code>BukkitTask</code> - Start the BukkitTask of LocationParticleChecker
     */
    private BukkitTask startLocationParticleChecker() {
        if (areParticlesDefined()) {
            Logging.info("Initiating ParticleTimeDrivenSystem");
            return new LocationChecker(this, config.getConfigProperty("BiomeEvents.Settings.PapiBiomePlaceholder"))
                .initializeParticleTimeDrivenSystem();
        } else {
            return null;
        }
    }

    /**
     * Get the plugins colored console prefix
     * @return <code>String</code> - The colored console prefix of this plugin
     */
    static String getConsolePrefix() {
        return consolePrefix;
    }

    /**
     * Get the current instance of this plugin
     * @return <code>BiomeEvents</code> - The current instance of this plugin
     */
    static BiomeEvents getInstance() {
        return instance;
    }

    /**
     * Get the current integer for PositionChecksInTicks
     * @return <code>Integer</code> - PositionChecksInTicks
     */
    public static Integer getPositionChecksInTicks() {
        return positionChecksInTicks;
    }

    /**
     * Get the current integer for PositionParticleChecksInTicks
     * @return <code>Integer</code> - PositionParticleChecksInTicks
     */
    public static Integer getPositionParticleChecksInTicks() {
        return positionParticleChecksInTicks;
    }

    /**
     * Return the current set BiomeModel list
     * @return <code>List&lt;BiomeModel&gt;</code> - List containing all BiomeModels from the config
     */
    static List<BiomeModel> getBiomeModels() {
        return biomeModels;
    }

    /**
     * Check whether particles have been defined in any of the BiomeModels
     * @return <code>Boolean</code> - Represents whether any particle has been defined or not.
     */
    private boolean areParticlesDefined() {
        return biomeModels.stream()
            .filter(x -> x.getWhileInBiomeEventModel().ParticleModels.stream().anyMatch(y -> !Objects.equals(y.getParticle(), "null")))
            .findFirst().orElse(null) != null;
    }

    /**
     * Check whether a single command model or all of them inside the biome configuration will be used
     * @return <code>Boolean</code> - <code>True</code> if one will get picked randomly, <code>false</code> if all will be used.
     */
    static boolean isSingleCommandModelEnabled() {
        return isSingleCommandModelEnabled;
    }
}
