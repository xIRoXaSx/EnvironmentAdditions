package net.lizardnetwork.environmentadditions.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import io.lumine.mythic.api.adapters.AbstractBossBar.BarColor;
import io.lumine.mythic.api.adapters.AbstractBossBar.BarStyle;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.bossbars.BukkitBossBar;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.lizardnetwork.environmentadditions.Logging;
import net.lizardnetwork.environmentadditions.helper.Parser;

public class ModelSpawnerMythicMobs {
    private final int level;
    private final ModelBossBar bar;

    public ModelSpawnerMythicMobs(int level, ModelBossBar bar) {
        this.level = level;
        this.bar = bar;
    }

    public void execute(Player target, @NotNull ModelSpawner spawner) {
        MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(spawner.getName()).orElse(null);
        if (mm == null) {
            Logging.warn("MythicMob with name " + spawner.getName() + " not found");
            return;
        }
        
        if (spawner.getScatter()) {
            for (int i = 0; i < spawner.getAmount(); ++i) {
                Location loc = spawner.getNextLocation(target, target.getLocation());
                if (loc != null) {
                    spawn(spawner, mm, target, loc);
                }
            }
            return;
        }

        Location loc = spawner.getNextLocation(target, target.getLocation());
        if (loc == null) {
            return;
        }
        for (int i = 0; i < spawner.getAmount(); ++i) {
            spawn(spawner, mm, target, loc);
        }
    }

    private void spawn(ModelSpawner spawner, MythicMob mm, Player targeted, Location loc) {
        ActiveMob mob = mm.spawn(BukkitAdapter.adapt(loc), this.level);
        if (spawner.getHealth() > 0) {
            mob.getEntity().setHealth(spawner.getHealth());
        }
        mob.setTarget(BukkitAdapter.adapt(targeted));

        if (this.bar != null && this.bar.getTitle() != "") {
            BukkitBossBar mmBar = new BukkitBossBar(this.bar.getTitle(), toMythicBossColor(), toMythicBossStyle());
            mob.addBar(this.bar.getTitle(), mmBar);
        }
    }

    private BarColor toMythicBossColor() {
        return Parser.valueOf(BarColor.class, this.bar.getColor().name().toUpperCase());
    }

    private BarStyle toMythicBossStyle() {
        return Parser.valueOf(BarStyle.class, this.bar.getStyle().name().toUpperCase());
    }
}
