package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import net.lizardnetwork.environmentadditions.interfaces.IModelExecutor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class ModelSound extends ModelCondition implements ICondition, IModelExecutor {
    private final String sound;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;
    private final boolean isGlobal;
    private final float maxRandomOffset;
    private final ModelCondition condition;

    public ModelSound(String sound, SoundCategory category, float volume, float pitch, boolean isGlobal, float maxRandomOffset, ModelCondition condition) {
        super(condition.isEnabled(), condition.getProbability(), condition.getFromTimeInTicks(), condition.getUntilTimeInTicks(),condition.getWeather(), condition.getPermission());
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
        this.isGlobal = isGlobal;
        this.maxRandomOffset = maxRandomOffset;
        this.condition = condition;
    }

    @Override
    public void execute(Player target) {
        Location src = target.getLocation();
        if (isGlobal) {
            target.getWorld().playSound(src, sound, volume, pitch);
            return;
        }
        target.playSound(src, sound, volume, pitch);
    }

    public String getSound() {
        return sound;
    }

    public SoundCategory getCategory() {
        return category;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public float getMaxRandomOffset() {
        return maxRandomOffset;
    }

    public ModelCondition getCondition() {
        return condition;
    }
}
