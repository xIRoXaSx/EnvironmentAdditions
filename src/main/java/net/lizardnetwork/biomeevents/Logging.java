package net.lizardnetwork.biomeevents;

import org.bukkit.Bukkit;

public class Logging {
    private static final String consolePrefix = "[" + BiomeEvents.getConsolePrefix() + "]";

    /**
     * Send info message to the logger
     * @param text String - The text to log
     */
    public static void info(String text) {
        Bukkit.getLogger().info(consolePrefix + " " + text);
    }

    /**
     * Send debug message to the logger
     * @param text String - The text to log
     */
    public static void debug(String text) {
        Bukkit.getLogger().info(consolePrefix + " DBG: " + text);
    }

    /**
     * Send warning message to the logger
     * @param text String - The text to log
     */
    public static void warning(String text) {
        Bukkit.getLogger().warning(consolePrefix + " " + text);
    }
}
