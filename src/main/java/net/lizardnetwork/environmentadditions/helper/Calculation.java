package net.lizardnetwork.environmentadditions.helper;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Calculation {
    public static Location calculateViewDirection(Player target, float vdd) {
        Location eyeLocation = target.getEyeLocation();
        Vector nv = eyeLocation.getDirection().normalize();
        double x = vdd * nv.getX() + eyeLocation.getX();
        double y = vdd * nv.getY() + eyeLocation.getY();
        double z = vdd * nv.getZ() + eyeLocation.getZ();
        return new Location(target.getWorld(), x, y, z);
    }
}
