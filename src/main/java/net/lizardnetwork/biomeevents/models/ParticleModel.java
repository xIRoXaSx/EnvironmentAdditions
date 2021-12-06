package net.lizardnetwork.biomeevents.models;

public class ParticleModel {
    private String particle;
    private String redstoneHexColor;
    private int redstoneSize;
    private int particleCount;
    private ConditionModel conditions = new ConditionModel();
    private ParticleAnimationModel particleAnimationModel;

    /**
     * Get the particle name to spawn
     * @return <code>String</code> - The particle name
     */
    public String getParticle() {
        return particle;
    }

    /**
     * Get the hex color of the redstone particle
     * @return <code>String</code> - The hex color of the redstone particle
     */
    public String getRedstoneHexColor() {
        return redstoneHexColor;
    }

    /**
     * Get the size of the redstone particle
     * @return <code>Int</code> - The size of the redstone particle
     */
    public int getRedstoneSize() {
        return redstoneSize;
    }

    /**
     * Get the particle name to spawn
     * @return <code>Int</code> - The count of particles to spawn
     */
    public int getParticleCount() {
        return particleCount;
    }

    /**
     * Get the ConditionModel of this ParticleModel
     * @return <code>ConditionModel</code> - The conditions which must apply in order to spawn particles
     */
    public ConditionModel getConditions() {
        return conditions;
    }

    /**
     * Get the ParticleAnimationModel if set
     * @return <code>ParticleAnimationModel</code> - The animations of the particle
     */
    public ParticleAnimationModel getParticleAnimationModel() {
        return particleAnimationModel;
    }

    /**
     * Set the particle to spawn
     * @param particle <code>String</code> - The name of the particle
     */
    public void setParticle(String particle) {
        this.particle = particle;
    }

    /**
     * Set the redstone particle hex color
     * @param redstoneHexColor <code>String</code> - The hex color of the particle
     */
    public void setRedstoneHexColor(String redstoneHexColor) {
        this.redstoneHexColor = redstoneHexColor;
    }

    /**
     * Set the redstone particle size
     * @param redstoneSize <code>Int</code> - The size of the redstone particle
     */
    public void setRedstoneSize(int redstoneSize) {
        this.redstoneSize = redstoneSize;
    }

    /**
     * Set the number of particles
     * @param particleCount <code>Int</code> - The number of particles to spawn
     */
    public void setParticleCount(int particleCount) {
        this.particleCount = particleCount;
    }

    /**
     * Set the ConditionModel of this ParticleModel
     */
    void setConditions(ConditionModel conditions) {
        this.conditions = conditions;
    }

    /**
     * Set the ParticleAnimationModel for advanced animations for the particle
     * @param particleAnimationModel <code>ParticleAnimationModel</code> - The ParticleAnimationModel for the animation
     */
    public void setParticleAnimationModel(ParticleAnimationModel particleAnimationModel) {
        this.particleAnimationModel = particleAnimationModel;
    }
}
