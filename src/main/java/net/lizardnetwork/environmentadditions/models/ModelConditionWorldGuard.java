package net.lizardnetwork.environmentadditions.models;

import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.World;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.lizardnetwork.environmentadditions.Logging;

public class ModelConditionWorldGuard {
    private final boolean matchAll;
    private final String[] inRgNames;
    private final String[] notInRgNames;

    public ModelConditionWorldGuard(boolean matchAll, String[] inRgName, String[] notInRgName) {
        this.matchAll = matchAll;
        this.inRgNames = inRgName;
        this.notInRgNames = notInRgName;
    }

    public boolean isConfigured() {
        return this.inRgNames.length > 0 || this.notInRgNames.length > 0;
    }

    public boolean isInside(Location target) {
        if (this.matchAll) {
            return isInsideEveryRegion(target);
        }
        return isInsideRegion(target);
    }

    public boolean isOutside(Location target) {
        return isOutsideEveryRegion(target);
    }

    private boolean isInsideRegion(Location target) {
        // If no region is defined, fall back to true.
        if (this.inRgNames.length == 0) {
            return true;
        }

        for (String rgName : this.inRgNames) {
            ProtectedRegion rg = getRegion(target, rgName);
            if (rg == null) {
                continue;
            }

            // Player needs to be in one of the specified regions.
            if (rg.contains(target.getBlockX(), target.getBlockY(), target.getBlockZ())) {
                return true;
            }
        }
        return false;
    }

    private boolean isInsideEveryRegion(Location target) {
        // If no region is defined, fall back to true.
        if (this.inRgNames.length == 0) {
            return true;
        }

        boolean inAll = true;
        for (String rgName : this.inRgNames) {
            ProtectedRegion rg = getRegion(target, rgName);
            if (rg == null) {
                continue;
            }

            // Player needs to be in every of the specified regions.
            inAll &= rg.contains(target.getBlockX(), target.getBlockY(), target.getBlockZ());
            if (!inAll) {
                return false;
            }
        }
        return true;
    }

    private boolean isOutsideEveryRegion(Location target) {
        // If no region is defined, fall back to true.
        if (this.notInRgNames.length == 0) {
            return true;
        }

        for (String rgName : this.notInRgNames) {
            ProtectedRegion rg = getRegion(target, rgName);
            if (rg == null) {
                continue;
            }

            // Player needs to be in no specified region.
            if (rg.contains(target.getBlockX(), target.getBlockY(), target.getBlockZ())) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private ProtectedRegion getRegion(Location target, String rgName) {
        World world;
        try {
            world = target.getWorld();
        } catch (IllegalArgumentException ex) {
            Logging.warn("World not loaded.");
            return null;
        }

        RegionContainer rgContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        if (rgContainer == null) {
            Logging.warn("Unable to get region container, is WorldGuard loaded?");
            return null;
        }

        RegionManager rgm = rgContainer.get(BukkitAdapter.adapt(world));
        if (rgm == null) {
            Logging.warn("Unable to get region manager for world " + world.getName());
            return null;
        }

        ProtectedRegion rg = rgm.getRegion(rgName);
        if (rg == null) {
            Logging.warn("No such region found: " + rgName + " in world: " + world.getName());
        }
        return rg;
    }
}
