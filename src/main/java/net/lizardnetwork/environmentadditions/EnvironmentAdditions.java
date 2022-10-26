package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import net.lizardnetwork.environmentadditions.helper.Resolve;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class EnvironmentAdditions extends JavaPlugin implements CommandExecutor {
    private static EnvironmentAdditions instance;
    private static final State state = new State();
    private static final String coloredPrefix = ChatColor.translateAlternateColorCodes('&', "&6Environment&aAddition&r");

    @Override
    public void onEnable() {
        long start = System.nanoTime();
        instance = this;
        state.setDependency(Resolve.resolveDependencies());
        state.setConfig();
        state.getConfig().linkConfigs();
        long end = System.nanoTime();
        Logging.info("Enabled within " + Math.round((end - start) / 1e6) + "ms");
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
