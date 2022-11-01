package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.models.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Observer {
    private final Plugin plugin;
    private BukkitTask observerTask;
    private long execTime;
    private long iter;

    public Observer(Plugin plugin) {
        this.plugin = plugin;
    }

    public void initTimeDrivenObserver(Player player, boolean benchmark) {
        observerTask = new BukkitRunnable() {
            @Override
            public void run() {
                long start = System.nanoTime();
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
                long end = System.nanoTime();
                if (benchmark) {
                    execTime += end - start;
                    iter++;
                }
            }
        }.runTaskTimer(plugin, 0, EnvironmentAdditions.getState().getSettings().getCheckTicks());
    }

    public BukkitTask getObserverTask() {
        return observerTask;
    }

    public long getIter() {
        return iter;
    }

    public long getAverageExecTime() {
        return execTime / iter;
    }
}
