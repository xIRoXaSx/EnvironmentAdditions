package net.lizardnetwork.environmentadditions.cmd;

import net.lizardnetwork.environmentadditions.interfaces.ICmd;
import org.bukkit.command.CommandSender;

public class Toggle extends CmdModel implements ICmd {
    public Toggle(String command, String permission, String[] aliases, String description) {
        super(command, permission, aliases, description);
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("Currently not implemented!");
        return true;
    }
}
