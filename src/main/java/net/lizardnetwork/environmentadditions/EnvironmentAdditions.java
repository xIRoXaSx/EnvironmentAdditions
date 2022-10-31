package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.helper.Resolve;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
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

public class EnvironmentAdditions extends JavaPlugin implements Listener, CommandExecutor {
    private static EnvironmentAdditions instance;
    private static final State state = new State();
    private static final String coloredPrefix = Parser.gradientText("EnvironmentAdditions", "#5ee667");

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        instance = this;
        state.setDependency(Resolve.resolveDependencies());
        state.setConfig();
        state.subscribeToEvents();
        long end = System.nanoTime();
        Logging.info("Enabled within " + Math.round((end - start) / 1e6) + "ms");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Observer observer = new Observer(this);
        EnvironmentAdditions.getState().appendObserverTask(
            event.getPlayer().getUniqueId(),
            observer.initTimeDrivenObserver(event.getPlayer())
        );
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        EnvironmentAdditions.getState().removeObserverTask(
            event.getPlayer().getUniqueId()
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        return new CmdHandler(sender, args).handle();
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

    public static void reload() {
        state.setConfig();
        Collection<? extends Player> online = instance.getServer().getOnlinePlayers();
        for (Player target : online) {
            Observer observer = new Observer(instance);
            EnvironmentAdditions.getState().appendObserverTask(
                target.getUniqueId(),
                observer.initTimeDrivenObserver(target)
            );
        }
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
