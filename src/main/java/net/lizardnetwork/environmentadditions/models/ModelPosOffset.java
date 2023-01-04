package net.lizardnetwork.environmentadditions.models;

public class ModelPosOffset {
    private final double relativeX;
    private final double relativeY;
    private final double relativeZ;

    public ModelPosOffset(double relX, double relY, double relZ) {
        this.relativeX = relX;
        this.relativeY = relY;
        this.relativeZ = relZ;
    }

    public double getRelativeX() {
        return relativeX;
    }

    public double getRelativeY() {
        return relativeY;
    }

    public double getRelativeZ() {
        return relativeZ;
    }
}