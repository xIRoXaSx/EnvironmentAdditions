package net.lizardnetwork.environmentadditions.models;

import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import net.lizardnetwork.environmentadditions.Logging;
import net.lizardnetwork.environmentadditions.helper.Calculation;
import net.lizardnetwork.environmentadditions.helper.Random;
import net.lizardnetwork.environmentadditions.interfaces.IModelExecutor;

public class ModelSpawner extends ModelCondition implements IModelExecutor {
    private final String name;
    private final int health;
    private final int amount;
    private final int radius;
    private final float viewDirectionDistance;
    private final boolean scatter;
    private final ModelPosOffset offset;
    private final ModelSpawnerMythicMobs mythicMobs;

    public ModelSpawner(
        String name,
        int health,
        int amount,
        int radius,
        float viewDirectionDistance,
        boolean scatter,
        ModelPosOffset offset,
        ModelCondition condition,
        @Nullable ModelSpawnerMythicMobs mythicMobs
    ) {
        super(
            condition.isEnabled(),
            condition.getProbability(),
            condition.getFromTimeInTicks(),
            condition.getUntilTimeInTicks(),
            condition.getWeather(),
            condition.getWorlds(),
            condition.getPermission(),
            condition.getLightCondition(),
            condition.getBlockCondition(),
            condition.getAreaCondition(),
            condition.getWorldGuardCondition(),
            condition.getDateCondition()
        );
        this.name = name;
        this.health = health;
        this.amount = amount;
        this.radius = radius;
        this.viewDirectionDistance = viewDirectionDistance;
        this.scatter = scatter;
        this.offset = offset;
        this.mythicMobs = mythicMobs;
    }

    @Override
    public void execute(Player target) {
        if (this.mythicMobs != null) {
            mythicMobs.execute(target);
            return;
        }

        if (this.getName() == "") {
            Logging.warn("MobSpawner names may not be empty");
            return;
        }
        
        EntityType entity;
        try {
            entity = EntityType.valueOf(this.name.toUpperCase());
        } catch (IllegalArgumentException ex) {
            Logging.warn("Mob with name " + this.name + " not found");
            return;
        }

        if (this.scatter) {
            for (int i = 0; i < this.getAmount(); ++i) {
                spawn(entity, target, getNextLocation(target, target.getLocation()));
            }
            return;
        }
        Location loc = getNextLocation(target, target.getLocation());
        for (int i = 0; i < this.getAmount(); ++i) {
            spawn(entity, target, loc);
        }
    }

    private void spawn(EntityType entity, Player targeted, Location loc) {
        Entity mob = loc.getWorld().spawnEntity(loc, entity, false);
        if (mob == null) {
            Logging.warn("Mob with name " + this.getName() + " not found");
            return;
        }
        if (this.health == 0) {
            return;
        }

        if (!(mob instanceof LivingEntity)) {
            Logging.warn("Entity " + entity.name() + " is not an instance of a living entity, health cannot be set!");
            return;
        }
        ((LivingEntity)mob).setHealth(health);
    }

    Location getNextLocation(Player targeted, Location loc) {
        if (this.getRadius() < 1) {
            return loc.add(
                new Random(-this.getRadius(), this.getRadius()).getFloatResult(),
                0,
                new Random(-this.getRadius(), this.getRadius()).getFloatResult()
            );
        }
        if (this.getViewDirectionDistance() > 0) {
            return Calculation.calculateViewDirection(targeted, this.getViewDirectionDistance());
        }
        if (this.getOffset() != null) {
            return loc.add(
                this.getOffset().getRelativeX(),
                this.getOffset().getRelativeY(),
                this.getOffset().getRelativeZ()
            );
        }
        return loc;
    }

    public String getName() {
        return this.name;
    }

    public int getHealth() {
        return this.health;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getRadius() {
        return this.radius;
    }

    public float getViewDirectionDistance() {
        return this.viewDirectionDistance;
    }

    public boolean getScatter() {
        return this.scatter;
    }

    public ModelPosOffset getOffset() {
        return this.offset;
    }
}
