package net.lizardnetwork.environmentadditions.models;

import org.bukkit.Location;

public class ModelConditionArea {
    private final ModelPosOffset min;
    private final ModelPosOffset max;
    private final boolean compareExactly;

    public ModelConditionArea(boolean compareExactly, ModelPosOffset min, ModelPosOffset max) {
        this.compareExactly = compareExactly;
        this.min = min;
        this.max = max;
    }

    public boolean isInArea(Location target) {
        double xCur = target.getX();
        double yCur = target.getY();
        double zCur = target.getZ();
        if (compareExactly) {
            return compare(xCur, min.getRelativeX(), max.getRelativeX()) &&
                compare(yCur, min.getRelativeY(), max.getRelativeY()) &&
                compare(zCur, min.getRelativeZ(), max.getRelativeZ());
        }
        return compareIgnoreZero(xCur, min.getRelativeX(), max.getRelativeX()) &&
            compareIgnoreZero(yCur, min.getRelativeY(), max.getRelativeY()) &&
            compareIgnoreZero(zCur, min.getRelativeZ(), max.getRelativeZ());
    }

    private boolean compare(double value, double min, double max) {
        return value >= min && value <= max;
    }

    private boolean compareIgnoreZero(double value, double min, double max) {
        boolean returnValue = true;
        if (min != 0) {
            returnValue = value >= min;
        }
        if (max != 0) {
            returnValue = returnValue && (value <= max);
        }
        return returnValue;
    }
}
