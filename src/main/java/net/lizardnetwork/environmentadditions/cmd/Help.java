package net.lizardnetwork.environmentadditions.cmd;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.interfaces.ICmd;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Help extends CmdModel implements ICmd {
    public Help(String command, String permission, String[] aliases, String description, String usage) {
        super(command, permission, aliases, description, usage);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, Plugin plugin) {
        // Since the default minecraft font isn't monospaced, magic is needed to correctly align the commands.
        double magic = 1.75;
        int len = getLongestCmd();
        TextComponent tc = new TextComponent();
        for (ICmd cmd : CmdHandler.registry) {
            String cmdText = cmd.getCmd();
            int delta = (int)(Math.abs(len - cmdText.length()) * magic);
            TextComponent component = new TextComponent(
                Parser.colorizeText("&f" + cmdText + " ".repeat(delta) + " &8» &7") + cmd.getDescription() + "\n"
            );
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd.getUsage()));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                Parser.colorizeText("Aliases: &7" + String.join("&f, &7", cmd.getAliases())) + "\n" +
                Parser.colorizeText("&fUsage:  &7") + cmd.getUsage()
            )));
            tc.addExtra(component);
        }
        sender.sendMessage("\n" + EnvironmentAdditions.getColoredPrefix() + " - Version: " + EnvironmentAdditions.getPluginDescription().getVersion() + "\n");
        sender.spigot().sendMessage(tc);
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
