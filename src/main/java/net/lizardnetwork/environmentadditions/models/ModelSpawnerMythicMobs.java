package net.lizardnetwork.environmentadditions.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import io.lumine.mythic.api.adapters.AbstractBossBar.BarColor;
import io.lumine.mythic.api.adapters.AbstractBossBar.BarStyle;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.bossbars.BukkitBossBar;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.lizardnetwork.environmentadditions.Logging;
import net.lizardnetwork.environmentadditions.interfaces.IModelExecutor;

public class ModelSpawnerMythicMobs implements IModelExecutor {
    private ModelSpawner spawner;
    private final int level;
    private final ModelBossBar bar;

    public ModelSpawnerMythicMobs(
        String name,
        int health,
        int amount,
        int radius,
        float viewDirectionDistance,
        boolean scatter,
        boolean safeLocation,
        int safeLocationHeight,
        int level,
        ModelPosOffset offset,
        ModelCondition condition,
        ModelBossBar bar
    ) {
        this.spawner = new ModelSpawner(name, health, amount, radius, viewDirectionDistance, scatter, safeLocation, safeLocationHeight, offset, condition, null);
        this.level = level;
        this.bar = bar;
    }

    @Override
    public void execute(Player target) {
        if (this.spawner.getName() == "") {
            Logging.warn("MythicMob names may not be empty");
            return;
        }

        MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(this.spawner.getName()).orElse(null);
        if (mm == null) {
            Logging.warn("MythicMob with name " + this.spawner.getName() + " not found");
            return;
        }
        
        if (this.spawner.getScatter()) {
            for (int i = 0; i < this.spawner.getAmount(); ++i) {
                spawn(mm, target, this.spawner.getNextLocation(target, target.getLocation()));
            }
            return;
        }
        Location loc = this.spawner.getNextLocation(target, target.getLocation());
        for (int i = 0; i < this.spawner.getAmount(); ++i) {
            spawn(mm, target, loc);
        }
    }

    private void spawn(MythicMob mm, Player targeted, Location loc) {
        ActiveMob mob = mm.spawn(BukkitAdapter.adapt(loc), this.level);
        if (this.spawner.getHealth() > 0) {
            mob.getEntity().setHealth(this.spawner.getHealth());
        }
        mob.setTarget(BukkitAdapter.adapt(targeted));

        if (this.bar != null && this.bar.getTitle() != "") {
            BukkitBossBar mmBar = new BukkitBossBar(this.bar.getTitle(), toMythicBossColor(), toMythicBossStyle());
            mob.addBar(this.bar.getTitle(), mmBar);
        }
    }

    private BarColor toMythicBossColor() {
        switch (this.bar.getColor()) {
            case BLUE:
                return BarColor.BLUE;
            case GREEN:
                return BarColor.GREEN;
            case PINK:
                return BarColor.PINK;
            case PURPLE:
                return BarColor.PURPLE;
            case RED:
                return BarColor.RED;
            case YELLOW:
                return BarColor.YELLOW;
            case WHITE:
            default:
                return BarColor.WHITE;
        }
    }

    private BarStyle toMythicBossStyle() {
        switch (this.bar.getStyle()) {
            case SEGMENTED_6:
                return BarStyle.SEGMENTED_6;
            case SEGMENTED_10:
                return BarStyle.SEGMENTED_10;
            case SEGMENTED_12:
                return BarStyle.SEGMENTED_12;
            case SEGMENTED_20:
                return BarStyle.SEGMENTED_20;
            case SOLID:
            default:
                return BarStyle.SOLID;
        }
    }
}
