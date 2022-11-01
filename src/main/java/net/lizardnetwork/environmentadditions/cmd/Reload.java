package net.lizardnetwork.environmentadditions.cmd;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.interfaces.ICmd;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Reload extends CmdModel implements ICmd {
    public Reload(String command, String permission, String[] aliases, String description, String usage) {
        super(command, permission, aliases, description, usage);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, Plugin plugin) {
        EnvironmentAdditions.reload();
        sender.sendMessage(EnvironmentAdditions.formPluginMessage(
            "&fConfigs have been &areloaded&f!"
        ));
        return true;
    }
}
