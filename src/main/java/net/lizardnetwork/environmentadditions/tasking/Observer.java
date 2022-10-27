package net.lizardnetwork.environmentadditions.tasking;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.models.ModelBiomeEvent;
import net.lizardnetwork.environmentadditions.models.ModelCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Observer {
    final Plugin plugin;

    public Observer(Plugin plugin) {
        this.plugin = plugin;
    }

    // TODO: Read "PositionChecksInTicks" from settings.
    BukkitTask initTimeDrivenObserver(Player player) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (ModelBiomeEvent event : EnvironmentAdditions.getState().getBiomeEvents()) {
                    if (!event.hasAnyValueFor(player)) {
                        continue;
                    }

                    for (ModelCommand modelCommand : event.getCommands()) {
                        if (!modelCommand.matchesEveryCondition(player)) {
                            continue;
                        }
                        modelCommand.execute(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
