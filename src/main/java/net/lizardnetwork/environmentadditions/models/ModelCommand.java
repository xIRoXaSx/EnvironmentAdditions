package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.interfaces.ICondition;

public class ModelCommand extends ModelCondition implements ICondition {
    private final String[] commands;
    private final ModelCondition condition;
    private final boolean pickRandomCommand;

    public ModelCommand(String[] commands, ModelCondition condition, boolean pickRandomCommand) {
        super(condition.isEnabled(), condition.getFromTimeInTicks(), condition.getUntilTimeInTicks(),condition.getWeather(), condition.getPermission());
        this.commands = commands;
        this.condition = condition;
        this.pickRandomCommand = pickRandomCommand;
    }

    public String[] getCommands() {
        return commands;
    }

    public ModelCondition getCondition() {
        return condition;
    }

    public boolean isRandom() {
        return pickRandomCommand;
    }
}
