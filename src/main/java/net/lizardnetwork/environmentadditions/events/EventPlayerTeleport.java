package net.lizardnetwork.environmentadditions.events;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EventPlayerTeleport implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        EnvironmentAdditions.getState().restartObserverTask(event.getPlayer());
    }
}
