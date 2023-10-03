package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import net.lizardnetwork.environmentadditions.enums.EDependency;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.helper.Resolve;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EnvironmentAdditions extends JavaPlugin implements Listener {
    private static EnvironmentAdditions instance;
    private static final State state = new State();
    private static final String coloredPrefix = Parser.gradientText("EnvironmentAdditions", "#5ee667");

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        instance = this;
        state.setConfig();
        
        // After the config has been read, determine the required dependencies.
        state.setDependencies();
        state.subscribeToEvents();
        List<String> deps = EDependency.parse(state.getDependencies()).stream().map(EDependency::toString).toList();
        List<String> optDeps = EDependency.parse(Resolve.hookableDependencies()).stream().map(EDependency::toString).toList();
        long end = System.nanoTime(); 

        Logging.info("Detected soft dependencies: " +  String.join(", ", optDeps));
        Logging.info("Used dependencies: " +  String.join(", ", deps));
        Logging.info("Enabled within " + Math.round((end - start) / 1e6) + "ms");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player target = event.getPlayer();
        addNewObserver(target, target.getUniqueId(), false);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        EnvironmentAdditions.getState().removeObserverTask(
            event.getPlayer().getUniqueId()
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        return new CmdHandler(sender, args, instance).handle(true);
    }

    public static String formPluginMessage(String value) {
        return EnvironmentAdditions.getColoredPrefix() + " » " + Parser.colorizeText(value);
    }

    public static long getBenchmarkAverageTime() {
        return getState().getBenchmarkAverageTime();
    }

    public static long getBenchmarkIterations() {
        return getState().getBenchmarkIterations();
    }

    public static void addNewObserver(Player target, UUID uuid, boolean benchmark) {
        if (target == null || !target.isOnline()) {
            return;
        }
        uuid = uuid == null ? target.getUniqueId() : uuid;
        Observer observer = new Observer(instance);
        observer.initTimeDrivenObserver(target, benchmark);
        EnvironmentAdditions.getState().appendObserverTask(uuid, observer);
    }

    public static void clearObservers() {
        getState().clearObservers();
    }

    public static void pauseObservers() {
        getState().pauseObservers();
    }

    public static void resumeObservers() {
        getState().resumeObservers();
    }

    public static void reload() {
        state.setConfig();
        Collection<? extends Player> online = instance.getServer().getOnlinePlayers();
        for (Player target : online) {
            addNewObserver(target, target.getUniqueId(), false);
        }
    }

    /**
     * Get the singleton Plugin instance.
     * @return Current instance.
     */
    static Plugin getInstance() {
        return instance;
    }

    public static State getState() {
        return state;
    }

    public static PluginDescriptionFile getPluginDescription() {
        return instance.getDescription();
    }

    public static String getColoredPrefix() {
        return coloredPrefix;
    }

    public static String getPrefix() {
        return instance.getDescription().getName();
    }
}
