package net.lizardnetwork.biomeevents.models;

public class ParticleAnimationModel {
    private Float viewDirectionDistance;
    private Float relativeOffsetX;
    private Float relativeOffsetY;
    private Float relativeOffsetZ;
    private ParticleLoopOptionModel loopOption;

    /**
     * Get the relative distance to the players looking direction for the animation
     * @return <code>Float</code> - The relative distance to the players looking direction
     */
    public Float getViewDirectionDistance() {
        return viewDirectionDistance;
    }

    /**
     * Get the relative x coord from which the animation should start
     * @return <code>Float</code> - The relative x coord from which the animation starts
     */
    public Float getRelativeOffsetX() {
        return relativeOffsetX;
    }

    /**
     * Get the relative y coord from which the animation should start
     * @return <code>Float</code> - The relative y coord from which the animation starts
     */
    public Float getRelativeOffsetY() {
        return relativeOffsetY;
    }

    /**
     * Get the relative z coord from which the animation should start
     * @return <code>Float</code> - The relative z coord from which the animation starts
     */
    public Float getRelativeOffsetZ() {
        return relativeOffsetZ;
    }

    /**
     * Get the loop option model of the animation section
     * @return <code>ParticleLoopOptionModel</code> - The ParticleLoopOptionModel for this particle section
     */
    public ParticleLoopOptionModel getLoopOption() {
        return loopOption;
    }

    /**
     * Set the relative distance to the players looking direction for the animation
     * @param viewDirectionDistance <code>Float</code> - The relative distance to the players looking direction
     */
    public void setViewDirectionDistance(Float viewDirectionDistance) {
        this.viewDirectionDistance = viewDirectionDistance;
    }

    /**
     * Set the relative x coord from which the animation should start
     */
    public void setRelativeOffsetX(Float relativeOffsetX) {
        this.relativeOffsetX = relativeOffsetX;
    }

    /**
     * Set the relative y coord from which the animation should start
     */
    public void setRelativeOffsetY(Float relativeOffsetY) {
        this.relativeOffsetY = relativeOffsetY;
    }

    /**
     * Set the relative z coord from which the animation should start
     */
    public void setRelativeOffsetZ(Float relativeOffsetZ) {
        this.relativeOffsetZ = relativeOffsetZ;
    }

    /**
     * Set the loop option model of the animation section
     * @param loopOption <code>ParticleLoopOptionModel</code> - The ParticleLoopOptionModel for this particle section
     */
    public void setLoopOption(ParticleLoopOptionModel loopOption) {
        this.loopOption = loopOption;
    }
}
