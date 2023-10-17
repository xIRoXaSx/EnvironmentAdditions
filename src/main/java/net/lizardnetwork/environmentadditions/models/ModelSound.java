package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.helper.Random;
import net.lizardnetwork.environmentadditions.interfaces.IModelExecutor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class ModelSound extends ModelCondition implements IModelExecutor {
    private final String sound;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;
    private final boolean isGlobal;
    private final float maxRandomOffset;
    private final ModelCondition condition;

    public ModelSound(String sound, SoundCategory category, float volume, float pitch, boolean isGlobal, float maxRandomOffset, ModelCondition condition) {
        super(
            condition.isEnabled(),
            condition.getProbability(),
            condition.getFromTimeInTicks(),
            condition.getUntilTimeInTicks(),
            condition.getWeather(),
            condition.getWorlds(),
            condition.getPermission(),
            condition.getLightCondition(),
            condition.getBlockCondition(),
            condition.getAreaCondition(),
            condition.getWorldGuardCondition(),
            condition.getDateCondition()
        );
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
        if (maxRandomOffset > 0) {
            src.setX(src.getX() + new Random(-maxRandomOffset, maxRandomOffset).getFloatResult());
            src.setZ(src.getZ() + new Random(-maxRandomOffset, maxRandomOffset).getFloatResult());
        }
        if (isGlobal) {
            target.getWorld().playSound(src, sound, category, volume, pitch);
            return;
        }
        target.playSound(src, sound, category, volume, pitch);
    }

    public ModelCondition getCondition() {
        return condition;
    }
}
