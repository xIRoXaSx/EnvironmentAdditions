package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import net.lizardnetwork.environmentadditions.enums.EDependency;
import net.lizardnetwork.environmentadditions.events.EventTabComplete;
import net.lizardnetwork.environmentadditions.models.ModelBiomeEvent;
import net.lizardnetwork.environmentadditions.models.ModelSettings;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class State {
    private Config config;
    private EDependency dependency;
    private ModelBiomeEvent[] biomeEvents;
    private ModelSettings settings;
    private final Map<UUID, BukkitTask> observerRunnables = new HashMap<>();

    public void setConfig() {
        config = new Config();
        settings = config.getSettings();
        biomeEvents = config.getLinkedConfigs();
        for (Map.Entry<UUID, BukkitTask> entrySet : observerRunnables.entrySet()) {
            removeObserverTask(entrySet.getKey());
        }
    }

    void subscribeToEvents() {
        Logging.info("Registering PlayerJoinEvent event");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)EnvironmentAdditions.getInstance(), EnvironmentAdditions.getInstance());

        EventTabComplete eventTabComplete = new EventTabComplete(CmdHandler.getCompletionArgs());
        Logging.info("Registering TabCompleter event");
        Bukkit.getServer().getPluginManager().registerEvents(eventTabComplete, EnvironmentAdditions.getInstance());
    }

    void setDependency(EDependency value) {
        this.dependency = value;
    }

    void appendObserverTask(UUID uuid, BukkitTask runnable) {
        observerRunnables.put(uuid, runnable);
    }

    void removeObserverTask(UUID uuid) {
        BukkitTask task = observerRunnables.get(uuid);
        if (task != null) {
            task.cancel();
        }
        observerRunnables.remove(uuid);
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
