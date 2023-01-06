package net.lizardnetwork.environmentadditions.models;

public class ModelParticleAnimation {
    private final float viewDirectionDistance;
    private final ModelPosOffset posOffset;
    private final ModelParticleLoop loopOption;

    public ModelParticleAnimation(float viewDirectionDistance, ModelPosOffset posOffset, ModelParticleLoop loopOption) {
        this.viewDirectionDistance = viewDirectionDistance;
        this.posOffset = posOffset;
        this.loopOption = loopOption;
    }

    public float getViewDirectionDistance() {
        return viewDirectionDistance;
    }

    public ModelPosOffset getPosOffset() {
        return posOffset;
    }

    public ModelParticleLoop getLoopOption() {
        return loopOption;
    }
}
