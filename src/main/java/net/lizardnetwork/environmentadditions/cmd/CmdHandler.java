package net.lizardnetwork.environmentadditions.cmd;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.interfaces.ICmd;
import net.lizardnetwork.environmentadditions.models.ModelCondition;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CmdHandler  {
    private static final String rootPerm = EnvironmentAdditions.getPluginDescription().getName().toLowerCase();
    final CommandSender sender;
    private final String[] args;
    private final Plugin plugin;
    final static ICmd[] registry = new ICmd[]{
        new Benchmark("benchmark", rootPerm + ".admin", new String[]{"bench", "b"}, "Benchmark your configuration."),
        new Reload("reload", rootPerm + ".admin", new String[]{"rl", "r"}, "Reload the configuration file."),
        new Help("help", rootPerm + ".help", new String[]{"hlp", "h"}, "Print this help message.")
    };

    public CmdHandler(@NotNull CommandSender sender, @NotNull String[] args, Plugin plugin) {
        this.sender = sender;
        this.args = args;
        this.plugin = plugin;
    }

    public boolean handle(boolean exec) {
        if (args.length == 0) {
            return true;
        }

        ICmd cmd = match(args[0]);
        if (cmd == null || !hasPermission(cmd.getPermission())) {
            return true;
        }
        return !exec || cmd.execute(sender, args, plugin);
    }

    private @Nullable ICmd match(String value) {
        for (ICmd cmd : registry) {
            if (cmd.getCmd().equalsIgnoreCase(value)) {
                return cmd;
            }
            for (String alias : cmd.getAliases()) {
                if (alias.equalsIgnoreCase(value)) {
                    return cmd;
                }
            }
        }
        return null;
    }

    /**
     * Get all arguments which can be used for tab-completion.
     * @return List&lt;String&gt; - A List of all arguments.
     */
    public static List<String> getCompletionArgs() {
        return Arrays.stream(registry).toList().stream().map(ICmd::getCmd).toList();
    }

    private boolean hasPermission(String perm) {
        return ModelCondition.hasPermission(sender, perm);
    }
}
