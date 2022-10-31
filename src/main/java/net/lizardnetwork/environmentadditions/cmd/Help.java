package net.lizardnetwork.environmentadditions.cmd;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.interfaces.ICmd;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Help extends CmdModel implements ICmd {
    public Help(String command, String permission, String[] aliases, String description) {
        super(command, permission, aliases, description);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, Plugin plugin) {
        StringBuilder msg = new StringBuilder("\n" + EnvironmentAdditions.getColoredPrefix() + " ")
            .append(" Version: ").append(EnvironmentAdditions.getPluginDescription().getVersion()).append("\n");
        int len = getLongestCmd();
        for (ICmd cmd : CmdHandler.registry) {
            int delta = Math.abs(len - cmd.getCmd().length());
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
            longest = Math.max(len, longest);
        }
        return longest;
    }
}
