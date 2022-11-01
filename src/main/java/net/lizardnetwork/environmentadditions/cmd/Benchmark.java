package net.lizardnetwork.environmentadditions.cmd;

import net.lizardnetwork.environmentadditions.EnvironmentAdditions;
import net.lizardnetwork.environmentadditions.helper.Parser;
import net.lizardnetwork.environmentadditions.interfaces.ICmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.UUID;

public class Benchmark extends CmdModel implements ICmd {
    public Benchmark(String command, String permission, String[] aliases, String description, String usage) {
        super(command, permission, aliases, description, usage);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, Plugin plugin) {
        sender.sendMessage(EnvironmentAdditions.formPluginMessage(
            "Depending on your current config setup, you may experience some lag."
        ));
        sender.sendMessage(EnvironmentAdditions.formPluginMessage(
            "If you want to customize the amount of &aiterations &fand &alength &fof the benchmark, " +
            "use &7/ea bench {iterations} {length in minutes}&f."
        ));
        sender.sendMessage(EnvironmentAdditions.formPluginMessage(
            "&cPausing &fcurrently runnning events."
        ));
        EnvironmentAdditions.pauseObservers();
        int numIter = 50;
        int numDur = 1;
        if (args.length > 1) {
            numIter = Parser.parse(args[1], numIter);
        }
        if (args.length > 2) {
            numDur = Parser.parse(args[2], numDur);
        }
        sender.sendMessage(EnvironmentAdditions.formPluginMessage(
            "Simulating &a" + numIter + " player" + (numIter > 1 ? "s" : "") +
            " &fon your current location for &a" + numDur + " minute" + (numDur > 1 ? "s" : "") + "&f."
        ));
        for (int i = 0; i < numIter; i++) {
            EnvironmentAdditions.addNewObserver((Player)sender, UUID.randomUUID(), true);
        }

       new BukkitRunnable() {
            @Override
            public void run() {
                long avg = EnvironmentAdditions.getBenchmarkAverageTime();
                long iter = EnvironmentAdditions.getBenchmarkIterations();
                EnvironmentAdditions.clearObservers();
                sender.sendMessage(EnvironmentAdditions.formPluginMessage(
                    "Benchmark &adone&f... resuming paused events."
                ));
                EnvironmentAdditions.resumeObservers();
                sender.sendMessage(EnvironmentAdditions.formPluginMessage(
                    "&aAverage &fexecution time over &a" + iter + "&f iterations: &a" +
                    Math.round((avg) / 1e6) + "ms &f(&a" + avg + "&f nano seconds)"
                ));
            }
        }.runTaskTimer(plugin, 20L * 60 * numDur, -1);
        return true;
    }
}
