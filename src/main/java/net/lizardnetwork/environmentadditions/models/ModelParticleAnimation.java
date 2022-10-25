package net.lizardnetwork.environmentadditions.models;

public class ModelParticleAnimation {
    private final float viewDirectionDistance;
    private final float relOffsetX;
    private final float relOffsetY;
    private final float relOffsetZ;
    private final ModelParticleLoop loopOption;

    public ModelParticleAnimation(float viewDirectionDistance, float relOffsetX, float relOffsetY, float relOffsetZ, ModelParticleLoop loopOption) {
        this.viewDirectionDistance = viewDirectionDistance;
        this.relOffsetX = relOffsetX;
        this.relOffsetY = relOffsetY;
        this.relOffsetZ = relOffsetZ;
        this.loopOption = loopOption;
    }

    public float getViewDirectionDistance() {
        return viewDirectionDistance;
    }

    public float getRelOffsetX() {
        return relOffsetX;
    }

    public float getRelOffsetY() {
        return relOffsetY;
    }

    public float getRelOffsetZ() {
        return relOffsetZ;
    }

    public ModelParticleLoop getLoopOption() {
        return loopOption;
    }
}
