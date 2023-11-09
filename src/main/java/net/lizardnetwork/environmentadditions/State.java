package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import net.lizardnetwork.environmentadditions.enums.EDependency;
import net.lizardnetwork.environmentadditions.events.EventPlayerTeleport;
import net.lizardnetwork.environmentadditions.events.EventTabComplete;
import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import net.lizardnetwork.environmentadditions.models.ModelBiomeEvent;
import net.lizardnetwork.environmentadditions.models.ModelSettings;
import net.lizardnetwork.environmentadditions.models.ModelSpawner;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class State {
    private Config config;
    private int dependency;
    private ModelBiomeEvent[] biomeEvents;
    private ModelSettings settings;
    private final Map<UUID, Observer> observers = new HashMap<>();
    private UUID[] pausedTasks = new UUID[0];

    void setConfig() {
        config = new Config();
        settings = config.getSettings();
        biomeEvents = config.getLinkedConfigs();

        if (pausedTasks.length == 0) {
            pauseObservers();
        } else {
            clearObservers();
        }
    }

    void setDependencies() {
        this.dependency = EDependency.None.getValue();
        String biomePlaceholder = this.settings.getBiomePlaceholder();
        if (biomePlaceholder != "") {
            this.dependency = EDependency.PlaceholderAPI.getValue();
        }

        boolean wg = false;
        boolean mm = false;
        for (ModelBiomeEvent be : this.biomeEvents) {
            ICondition[][] allIConds = new ICondition[][] {be.getCommands(), be.getParticles(), be.getSounds(), be.getSpawners()};
            allConds:
            for (ICondition[] iconds : allIConds) {
                for (ICondition cond : iconds) {
                    if (cond.isWorldGuardConfigured()) {
                        this.dependency += EDependency.WorldGuard.getValue();
                        wg = true;
                        break allConds;
                    }
                }
            }

            if (be.isWorldGuardConfigured() && !wg) {
                this.dependency += EDependency.WorldGuard.getValue();
                wg = true;
            }

            for (ModelSpawner spawner : be.getSpawners()) {
                if (spawner.isMythicMobsMob() && !mm) {
                    this.dependency += EDependency.MythicMobs.getValue();
                    mm = true;
                    break;
                }
            }

            if (wg && mm) {
                // All dependencies detected.
                return;
            }
        }
    }

    void subscribeToEvents() {
        Logging.info("Registering events.");
        JavaPlugin instance = (JavaPlugin)EnvironmentAdditions.getInstance();
        Bukkit.getServer().getPluginManager().registerEvents((Listener)EnvironmentAdditions.getInstance(), instance);
        Bukkit.getServer().getPluginManager().registerEvents(new EventPlayerTeleport(), instance);
        PluginCommand command = instance.getCommand(instance.getName());
        if (command != null) {
            command.setTabCompleter(new EventTabComplete(CmdHandler.getCompletionArgs()));
        }
    }

    long getBenchmarkAverageTime() {
        long avg = 0;
        for (Map.Entry<UUID, Observer> entry : observers.entrySet()) {
            avg += entry.getValue().getAverageExecTime();
        }
        return avg / observers.entrySet().size();
    }

    long getBenchmarkIterations() {
        long iter = 0;
        for (Map.Entry<UUID, Observer> entry : observers.entrySet()) {
            iter += entry.getValue().getIter();
        }
        return iter;
    }

    void appendObserverTask(UUID uuid, Observer observer) {
        observers.put(uuid, observer);
    }

    void removeObserverTask(UUID uuid) {
        Observer observer = observers.get(uuid);
        if (observer != null) {
            observer.getObserverTask().cancel();
        }
        observers.remove(uuid);
    }

    public void restartObserverTask(Player target) {
        UUID uuid = target.getUniqueId();
        removeObserverTask(uuid);
        EnvironmentAdditions.addNewObserver(target, uuid, false);
    }

    void pauseObservers() {
        List<UUID> uuids = new ArrayList<>();
        for (Map.Entry<UUID, Observer> entry : observers.entrySet()) {
            UUID uuid = entry.getKey();
            uuids.add(uuid);
        }
        clearObservers();
        this.pausedTasks = uuids.toArray(new UUID[0]);
    }

    void resumeObservers() {
        if (pausedTasks.length == 0) {
            return;
        }
        for (UUID uuid : pausedTasks) {
            Observer observer = new Observer(EnvironmentAdditions.getInstance());
            Player target = Bukkit.getServer().getPlayer(uuid);
            if (target == null || !target.isOnline()) {
                continue;
            }
            observer.initTimeDrivenObserver(target, false);
            appendObserverTask(target.getUniqueId(), observer);
        }
        pausedTasks = new UUID[0];
    }

    void clearObservers() {
        for (Map.Entry<UUID, Observer> entry : observers.entrySet()) {
            entry.getValue().getObserverTask().cancel();
        }
        observers.clear();
    }

    public String getBiomePlaceholder() {
        return settings.getBiomePlaceholder();
    }

    public int getDependencies() {
        return dependency;
    }

    ModelBiomeEvent[] getBiomeEvents() {
        return biomeEvents;
    }

    ModelSettings getSettings() {
        return settings;
    }
}
