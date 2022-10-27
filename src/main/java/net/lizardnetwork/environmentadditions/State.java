package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import net.lizardnetwork.environmentadditions.enums.Dependency;
import net.lizardnetwork.environmentadditions.events.EventTabComplete;
import net.lizardnetwork.environmentadditions.models.ModelBiomeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class State {
    private Config config;
    private Dependency dependency;
    private ModelBiomeEvent[] biomeEvents;
    private final Map<UUID, BukkitTask> observerRunnables = new HashMap<>();

    public void setConfig() {
        config = new Config();
        biomeEvents = config.getLinkedConfigs();
    }

    void subscribeToEvents() {
        Logging.info("Registering PlayerJoinEvent event");
        Bukkit.getServer().getPluginManager().registerEvents((Listener)EnvironmentAdditions.getInstance(), EnvironmentAdditions.getInstance());

        EventTabComplete eventTabComplete = new EventTabComplete(CmdHandler.getCompletionArgs());
        Logging.info("Registering TabCompleter event");
        Bukkit.getServer().getPluginManager().registerEvents(eventTabComplete, EnvironmentAdditions.getInstance());
    }

    void setDependency(Dependency value) {
        this.dependency = value;
    }

    void setBiomeEvents(ModelBiomeEvent[] biomeEvents) {
        this.biomeEvents = biomeEvents;
    }

    void appendObserver(UUID uuid, BukkitTask runnable) {
        observerRunnables.put(uuid, runnable);
    }

    public Config getConfig() {
        return config;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public ModelBiomeEvent[] getBiomeEvents() {
        return biomeEvents;
    }
}
