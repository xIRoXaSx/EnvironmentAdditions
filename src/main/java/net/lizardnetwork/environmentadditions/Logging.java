package net.lizardnetwork.environmentadditions;

import org.bukkit.Bukkit;
import java.util.logging.Level;

public class Logging {
    private static final String consolePrefix = "[" + EnvironmentAdditions.getPrefix() + "] ";

    public static void info(String msg) {
        Bukkit.getLogger().log(Level.INFO, consolePrefix + msg);
    }

    public static void debug(String msg) {
        Bukkit.getLogger().log(Level.INFO, consolePrefix + "DBG: " + msg);
    }

    public static void warn(String msg) {
        Bukkit.getLogger().log(Level.WARNING, consolePrefix + msg);
    }
}
