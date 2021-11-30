package net.lizardnetwork.biomeevents.models;

public class ParticleLoopOptionModel {
    private int chanceForEachLoop;
    private int radiusInBlocks;

    /**
     * Get the chance of spawning a particle for the loop
     * @return <code>Int</code> - The chance of spawning particles
     */
    public int getChanceForEachLoop() {
        return chanceForEachLoop;
    }

    /**
     * Get the radius for spawning a particle
     * @return <code>Int</code> - The radius for spawning a particle
     */
    public int getRadiusInBlocks() {
        return radiusInBlocks;
    }

    /**
     * Set the chance of spawning a particle for the loop
     * @param chanceForEachLoop <code>Int</code> - The chance of spawning particles
     */
    public void setChanceForEachLoop(int chanceForEachLoop) {
        this.chanceForEachLoop = chanceForEachLoop;
    }

    /**
     * Set the radius for spawning a particle
     * @param radiusInBlocks <code>Int</code> - The radius for spawning a particle
     */
    public void setRadiusInBlocks(int radiusInBlocks) {
        this.radiusInBlocks = radiusInBlocks;
    }
}
