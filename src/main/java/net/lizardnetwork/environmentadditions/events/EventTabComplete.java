package net.lizardnetwork.environmentadditions.events;

import net.lizardnetwork.environmentadditions.cmd.CmdHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class EventTabComplete implements Listener, TabCompleter {
    List<String> commands;

    public EventTabComplete(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        CmdHandler handler = new CmdHandler(sender, args);
        if (!handler.handle())
            return null;
        if (args.length == 1)
            return commands;
        return null;
    }
}
