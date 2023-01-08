package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.Logging;
import net.lizardnetwork.environmentadditions.enums.ECommandExecutor;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.helper.Placeholder;
import net.lizardnetwork.environmentadditions.helper.Random;
import net.lizardnetwork.environmentadditions.interfaces.ICondition;
import net.lizardnetwork.environmentadditions.interfaces.IModelExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModelCommand extends ModelCondition implements ICondition, IModelExecutor {
    private final String[] commands;
    private final ECommandExecutor executor;
    private final ModelCondition condition;
    private final boolean pickRandomCommand;

    public ModelCommand(String[] commands, ECommandExecutor executor, ModelCondition condition, boolean pickRandomCommand) {
        super(
            condition.isEnabled(),
            condition.getProbability(),
            condition.getFromTimeInTicks(),
            condition.getUntilTimeInTicks(),
            condition.getWeather(),
            condition.getPermission(),
            condition.getLightCondition(),
            condition.getBlockCondition()
        );
        this.commands = commands;
        this.executor = executor;
        this.condition = condition;
        this.pickRandomCommand = pickRandomCommand;
    }

    /**
     * Execute one / every command.
     * @param target CommandSender - The target of the command.
     */
    @Override
    public void execute(Player target) {
        int commandsLength = getCommands().length;
        if (commandsLength == 0) {
            Logging.warn("No command to execute configured!");
            return;
        }

        if (isRandom() && commandsLength > 1) {
            int ind = new Random(0, commandsLength).getIntResult();
            String command = getCommands()[ind];
            dispatchCommand(command, target);
            return;
        }

        for (String command : getCommands()) {
            dispatchCommand(command,target);
        }
    }

    private void dispatchCommand(String command, CommandSender target) {
        StringBuilder sb = new StringBuilder("Executing command \"");
        String replacedCommand = new Placeholder(command).resolve((Player)target).getReplaced();
        replacedCommand = Parser.colorizeText(replacedCommand);
        sb.append(command).append("\" as ").append(executor.toString());
        if (executor.equals(ECommandExecutor.PLAYER)) {
            sb.append(" for ").append(target.getName());
        }
        Logging.info(sb.toString());

        if (executor.equals(ECommandExecutor.PLAYER)) {
            ((Player)target).performCommand(replacedCommand);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacedCommand);
        }
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
