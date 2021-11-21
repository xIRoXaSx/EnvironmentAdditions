package net.lizardnetwork.biomeevents.helper;

import net.lizardnetwork.biomeevents.BiomeEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LocationChecker {
    final Plugin plugin;

    public LocationChecker(Plugin plugin) {
        this.plugin = plugin;
    }

    private BukkitTask initializeTimeDrivenSystem() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                var onlinePlayers = plugin.getServer().getOnlinePlayers();
                    for (var player : onlinePlayers) {
                        // Check biome of user
                        player.getLocation().getBlock().getBiome();
                    }
            }
        }.runTaskTimer(plugin, 0, BiomeEvents.getPositionChecksInTicks());
    }
}
