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
    public Benchmark(String command, String permission, String[] aliases, String description) {
        super(command, permission, aliases, description);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args, Plugin plugin) {
        sender.sendMessage(EnvironmentAdditions.formPluginMessage(
            "Depending on your current config setup, you may experience some lag."
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
            " &fon your current location for " + numDur + " &aminute" + (numDur > 1 ? "s" : "") + "&f."
        ));
        for (int i = 0; i < numIter; i++) {
            EnvironmentAdditions.addNewObserver((Player)sender, UUID.randomUUID());
        }

       new BukkitRunnable() {
            @Override
            public void run() {
                EnvironmentAdditions.clearObservers();
                sender.sendMessage(EnvironmentAdditions.formPluginMessage(
                    "Benchmark &adone&f... resuming paused events."
                ));
                EnvironmentAdditions.resumeObservers();
            }
        }.runTaskTimer(plugin, 20L * 60 * numDur, -1);
        return true;
    }
}
