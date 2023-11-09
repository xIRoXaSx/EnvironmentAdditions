package net.lizardnetwork.environmentadditions.models;

import java.util.Collection;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import net.lizardnetwork.environmentadditions.Logging;
import net.lizardnetwork.environmentadditions.helper.Calculation;
import net.lizardnetwork.environmentadditions.helper.Random;
import net.lizardnetwork.environmentadditions.interfaces.IModelExecutor;

public class ModelSpawner extends ModelCondition implements IModelExecutor {
    private final int maxSafeLocationDrift = 10;

    private final String name;
    private final int health;
    private final int amount;
    private final int radius;
    private final float viewDirectionDistance;
    private final boolean scatter;
    private final boolean safeLocation;
    private final int safeLocationHeight;
    private final ModelPosOffset offset;
    private final ModelSpawnerMythicMobs mythicMobs;

    public ModelSpawner(
        String name,
        int health,
        int amount,
        int radius,
        float viewDirectionDistance,
        boolean scatter,
        boolean safeLocation,
        int safeLocationHeight,
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
        this.safeLocation = safeLocation;
        this.safeLocationHeight = safeLocationHeight;
        this.offset = offset;
        this.mythicMobs = mythicMobs;
    }

    @Override
    public void execute(Player target) {
        if (this.mythicMobs != null) {
            mythicMobs.execute(target, this);
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
                Location loc = getNextLocation(target, target.getLocation());
                if (loc != null) {
                    spawn(entity, target, loc);
                }
            }
            return;
        }

        Location loc = getNextLocation(target, target.getLocation());
        if (loc == null) {
            return;
        }
        for (int i = 0; i < this.getAmount(); ++i) {
            spawn(entity, target, loc);
        }
    }

    @Nullable
    private Location nextSafeLocation(Location loc) {
        if (isLocationSafe(loc)) {
            return addBlockHeight(loc);
        }

        for (int i = 0; i < this.maxSafeLocationDrift; i++) {
            Location l = loc.clone().add(0, i, 0);
            if (isLocationSafe(l)) {
                return addBlockHeight(l);
            }
            l = l.add(0, -(2*i), 0);
            if (isLocationSafe(l)) {
                return addBlockHeight(l);
            }
        }
        return null;
    }

    private boolean isLocationSafe(Location loc) {
        Material[] mats = new Material[]{Material.AIR, Material.CAVE_AIR, Material.VOID_AIR};
        Location lo = loc.clone().add(0, -1, 0);
        for (int i = 0; i < mats.length; i++) {
            // Block below must not be air.
            if (lo.getBlock().getType().equals(mats[i])) {
                return false;
            }
        }
        return hasMaterialAbove(loc.clone(), mats);
    }

    private boolean hasMaterialAbove(Location loc, Material[] mats) {
        Location l = loc.clone();
        boolean hasMatAbove = true;
        for (float i = 0; i < this.safeLocationHeight; i++) {
            boolean matched = false;
            for (int j = 0; j < mats.length; j++) {
                matched |= l.add(0, i, 0).getBlock().getType().equals(mats[j]);
            }
            hasMatAbove &= matched;
            if (!hasMatAbove) {
                return false;
            }
        }
        return true;
    }

    private Location addBlockHeight(Location loc) {
        Collection<BoundingBox> bbs = loc.getBlock().getCollisionShape().getBoundingBoxes();
        if (bbs.size() == 0) {
            return loc;
        }
        BoundingBox bb = bbs.iterator().next();
        if (bb == null) {
            return loc;
        }
        return loc.add(0, bb.getHeight(), 0);
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

    @Nullable
    Location getNextLocation(Player targeted, Location loc) {
        Location l = loc;
        if (this.getRadius() > 1) {
            l = loc.add(
                new Random(-this.getRadius(), this.getRadius()).getFloatResult(),
                0,
                new Random(-this.getRadius(), this.getRadius()).getFloatResult()
            );
        } else if (this.getViewDirectionDistance() > 0) {
            l = Calculation.calculateViewDirection(targeted, this.getViewDirectionDistance());
        } else if (this.getOffset() != null) {
            l = loc.add(
                this.getOffset().getRelativeX(),
                this.getOffset().getRelativeY(),
                this.getOffset().getRelativeZ()
            );
        } 
        if (this.safeLocation) {
            l = nextSafeLocation(l);
        }
        return l;
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
