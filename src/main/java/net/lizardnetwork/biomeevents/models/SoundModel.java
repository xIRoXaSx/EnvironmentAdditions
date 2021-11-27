package net.lizardnetwork.biomeevents.models;

public class SoundModel {
    private int chance = 1;
    private String sound;
    private String category = "Ambient";
    private Float volume = 1.0f;
    private Float pitch = 1.0f;
    private boolean isServerWide = false;
    private String permission = "";
    private double maxRandomOffset = -1d;
    private ConditionModel conditions = new ConditionModel();

    /**
     * Get the chance of this SoundModel
     * @return <code>Int</code> - The chance
     */
    public int getChance() {
        return chance;
    }

    /**
     * Get the sound of this SoundModel
     * @return <code>String</code> - The sound
     */
    public String getSound() {
        return sound;
    }

    /**
     * Get the sound category of this SoundModel
     * @return <code>String</code> - The sound category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get the sound volume of this SoundModel
     * @return <code>Float</code> - The sound volume
     */
    public Float getVolume() {
        return volume;
    }

    /**
     * Get the sound pitch of this SoundModel
     * @return <code>Float</code> - The sound pitch
     */
    public Float getPitch() {
        return pitch;
    }

    /**
     * Get the boolean which represents the sound target of this SoundModel
     * @return <code>Boolean</code> - Whether the sound should be played for the player or in the world
     */
    public boolean isServerWide() {
        return isServerWide;
    }

    /**
     * Get the permission needed for this SoundModel to be heard / spawned
     * @return <code>String</code> - The permission needed to spawn the sound
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Get the maximum offset where the sound of this SoundModel will be spawned.
     * Configuring a value will overwrite isServerWide to <code>true</code>!
     * @return <code>Double</code> - The maximum offset the sound can spawn away from the user
     */
    public double getMaxRandomOffset() {
        return maxRandomOffset;
    }

    /**
     * Get the ConditionModel of this SoundModel
     * @return <code>ConditionModel</code> - The conditions which must apply in order to spawn the sound
     */
    public ConditionModel getConditions() {
        return conditions;
    }

    /**
     * Set the chance of this SoundModel
     */
    void setChance(int chance) {
        this.chance = chance;
    }

    /**
     * Set the sound of this SoundModel
     */
    void setSound(String sound) {
        this.sound = sound;
    }

    /**
     * Set the sound category of this SoundModel
     */
    void setCategory(String category) {
        this.category = category;
    }

    /**
     * Set the sound volume of this SoundModel
     */
    void setVolume(Float volume) {
        this.volume = volume;
    }

    /**
     * Set the sound pitch of this SoundModel
     */
    void setPitch(Float pitch) {
        this.pitch = pitch;
    }

    /**
     * Set the boolean which represents the sound target of this SoundModel
     */
    void setIsServerWide(boolean isServerWide) {
        this.isServerWide = isServerWide;
    }

    /**
     * Set the permission needed for this SoundModel to be heard / spawned
     */
    void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Set the maximum offset where the sound of this SoundModel will be spawned.
     * Configuring a value will overwrite isServerWide to <code>true</code>!
     */
    void setMaxRandomOffset(double maxRandomOffset) {
        this.maxRandomOffset = maxRandomOffset;
        this.isServerWide = true;
    }

    /**
     * Set the ConditionModel of this SoundModel
     */
    void setConditions(ConditionModel conditions) {
        this.conditions = conditions;
    }
}
