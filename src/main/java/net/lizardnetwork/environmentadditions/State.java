package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import net.lizardnetwork.environmentadditions.enums.EDependency;
import net.lizardnetwork.environmentadditions.events.EventTabComplete;
import net.lizardnetwork.environmentadditions.models.ModelBiomeEvent;
import net.lizardnetwork.environmentadditions.models.ModelSettings;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;

public class State {
    private Config config;
    private EDependency dependency;
    private ModelBiomeEvent[] biomeEvents;
    private ModelSettings settings;
    private final Map<UUID, BukkitTask> observerTasks = new HashMap<>();
    private UUID[] pausedTasks = new UUID[0];

    public void setConfig() {
        config = new Config();
        settings = config.getSettings();
        biomeEvents = config.getLinkedConfigs();
        for (Map.Entry<UUID, BukkitTask> entrySet : observerTasks.entrySet()) {
            removeObserverTask(entrySet.getKey());
        }
    }

    void subscribeToEvents() {
        Logging.info("Registering PlayerJoinEvent event");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)EnvironmentAdditions.getInstance(), EnvironmentAdditions.getInstance());

        Logging.info("Registering TabCompleter event");
        JavaPlugin instance = (JavaPlugin)EnvironmentAdditions.getInstance();
        PluginCommand command = instance.getCommand(instance.getName());
        if (command != null) {
            command.setTabCompleter(new EventTabComplete(CmdHandler.getCompletionArgs()));
        }
    }

    void setDependency(EDependency value) {
        this.dependency = value;
    }

    void appendObserverTask(UUID uuid, BukkitTask runnable) {
        observerTasks.put(uuid, runnable);
    }

    void removeObserverTask(UUID uuid) {
        BukkitTask task = observerTasks.get(uuid);
        if (task != null) {
            task.cancel();
        }
        observerTasks.remove(uuid);
    }

    void pauseObservers() {
        List<UUID> uuids = new ArrayList<>();
        for (Map.Entry<UUID, BukkitTask> entry : observerTasks.entrySet()) {
            UUID uuid = entry.getKey();
            uuids.add(uuid);
            removeObserverTask(uuid);
        }
        this.pausedTasks = uuids.toArray(new UUID[0]);
    }

    void resumeObservers() {
        if (pausedTasks.length < 1) {
            return;
        }
        for (UUID uuid : pausedTasks) {
            Observer observer = new Observer(EnvironmentAdditions.getInstance());
            Player target = Bukkit.getServer().getPlayer(uuid);
            if (target == null || !target.isOnline()) {
                continue;
            }
            appendObserverTask(target.getUniqueId(),observer.initTimeDrivenObserver(target));
        }
        pausedTasks = new UUID[0];
    }

    void clearObservers() {
        for (Map.Entry<UUID,BukkitTask> entry : observerTasks.entrySet()) {
            entry.getValue().cancel();
        }
        observerTasks.clear();
    }

    public String getBiomePlaceholder() {
        return settings.getBiomePlaceholder();
    }

    public EDependency getDependency() {
        return dependency;
    }

    ModelBiomeEvent[] getBiomeEvents() {
        return biomeEvents;
    }

    ModelSettings getSettings() {
        return settings;
    }
}
