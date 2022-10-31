package net.lizardnetwork.environmentadditions.cmd;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.interfaces.ICmd;
import org.bukkit.command.CommandSender;

public class Help extends CmdModel implements ICmd {
    public Help(String command, String permission, String[] aliases, String description) {
        super(command, permission, aliases, description);
    }

    @Override
    public boolean execute(CommandSender sender) {
        StringBuilder msg = new StringBuilder("\n" + EnvironmentAdditions.getColoredPrefix() + "\n")
            .append(" Version: ").append(EnvironmentAdditions.getPluginDescription().getVersion());
        int len = getLongestCmd();
        for (ICmd cmd : CmdHandler.registry) {
            int delta = Math.abs(cmd.getCmd().length() - len);
            msg.append("&f").append(cmd.getCmd()).append(" ".repeat(delta))
                .append(" &8» &7").append(cmd.getDescription())
                .append("\n\n");
        }

        sender.sendMessage(Parser.colorizeText(msg.toString()));
        return true;
    }

    private int getLongestCmd() {
        int longest = 0;
        for (ICmd cmd : CmdHandler.registry) {
            int len = cmd.getCmd().length();
            if (len > longest) {
                longest = len;
            }
        }
        return longest;
    }
}
