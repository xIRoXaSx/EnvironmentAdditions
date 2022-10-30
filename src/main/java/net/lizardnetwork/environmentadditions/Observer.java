package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.models.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Observer {
    final Plugin plugin;

    public Observer(Plugin plugin) {
        this.plugin = plugin;
    }

    public BukkitTask initTimeDrivenObserver(Player player) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (ModelBiomeEvent event : EnvironmentAdditions.getState().getBiomeEvents()) {
                    if (!event.hasAnyValueFor(player)) {
                        continue;
                    }

                    for (ModelCommand model : event.getCommands()) {
                        if (!model.matchesEveryCondition(player)) {
                            continue;
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> model.execute(player), 0);
                    }

                    for (ModelParticle model : event.getParticles()) {
                        if (!model.matchesEveryCondition(player)) {
                            continue;
                        }
                        model.execute(player);
                    }

                    for (ModelSound model : event.getSounds()) {
                        if (!model.matchesEveryCondition(player)) {
                            continue;
                        }
                        model.execute(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, EnvironmentAdditions.getState().getSettings().getCheckTicks());
    }
}
