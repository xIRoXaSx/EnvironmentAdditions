package net.lizardnetwork.environmentadditions.cmd;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.interfaces.ICmd;
import org.bukkit.command.CommandSender;

public class Reload extends CmdModel implements ICmd {
    public Reload(String command, String permission, String[] aliases, String description) {
        super(command, permission, aliases, description);
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Currently not implemented!");
        EnvironmentAdditions.reload();
        return true;
    }
}
