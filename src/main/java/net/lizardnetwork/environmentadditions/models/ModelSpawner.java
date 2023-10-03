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

public class ModelSpawner extends ModelCondition implements IModelExecutor {
    private final String name;
    private final Integer health;
    private final Integer amount;
    private final Integer level;
    private final Integer despawnTime; // Time in ticks.
    private final ModelBossBar bar;
    private final ModelPosOffset offset;

    public ModelSpawner(String name, Integer health, Integer amount, Integer level, Integer despawnTime, ModelBossBar bar, ModelCondition condition, ModelPosOffset offset) {
        super(
            condition.isEnabled(),
            condition.getProbability(),
            condition.getFromTimeInTicks(),
            condition.getUntilTimeInTicks(),
            condition.getWeather(),
            condition.getPermission(),
            condition.getLightCondition(),
            condition.getBlockCondition(),
            condition.getAreaCondition(),
            condition.getWorldGuardCondition()
        );
        this.name = name;
        this.health = health;
        this.amount = amount;
        this.level = level;
        this.despawnTime = despawnTime;
        this.bar = bar;
        this.offset = offset;
    }

    @Override
    public void execute(Player target) {
        if (this.name == "") {
            Logging.warn("MythicMob names may not be empty");
            return;
        }

        MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(this.name).orElse(null);
        if (mm == null) {
            Logging.warn("MythicMob with name " + this.name + " not found");
            return;
        }
        for (int i = 0; i < this.amount; ++i) {            
            spawn(mm, target.getLocation());
        }
    }

    private void spawn(MythicMob mm, Location target) {       
        ActiveMob mob = mm.spawn(BukkitAdapter.adapt(target), this.level);
        if (this.health > 0) {
            mob.getEntity().setHealth(this.health);
        }

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
