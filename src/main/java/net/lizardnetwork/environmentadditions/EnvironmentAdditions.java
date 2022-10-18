package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class EnvironmentAdditions extends JavaPlugin implements CommandExecutor {
    private static EnvironmentAdditions instance;
    private static Config config;

    @Override
    public void onEnable() {
        long now = System.nanoTime();
        instance = this;
        //config = new Config();
        getLogger().log(Level.INFO, "Enabled within " + ((System.nanoTime() - now) / 1e6) + "ms");
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

    public static void reload() {
        //config = new Config();
    }

    public static PluginDescriptionFile getPluginDescription() {
        return instance.getDescription();
    }
}
