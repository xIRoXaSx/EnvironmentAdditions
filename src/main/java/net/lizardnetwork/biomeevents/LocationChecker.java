package net.lizardnetwork.biomeevents;

import net.lizardnetwork.biomeevents.external.PlaceholderApiHook;
import net.lizardnetwork.biomeevents.helper.ChanceCalculation;
import net.lizardnetwork.biomeevents.helper.Parser;
import net.lizardnetwork.biomeevents.models.SoundModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class LocationChecker {
    private final Plugin plugin;
    private final String papiBiomePlaceholder;

    public LocationChecker(Plugin plugin, String papiBiomePlaceholder) {
        this.plugin = plugin;
        this.papiBiomePlaceholder = papiBiomePlaceholder;
    }

    BukkitTask initializeTimeDrivenSystem() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                var onlinePlayers = plugin.getServer().getOnlinePlayers();

                for (Player player : onlinePlayers) {
                    boolean biomePlaceholderEmpty = papiBiomePlaceholder == null || papiBiomePlaceholder.isBlank();
                    String biomePlaceholder = biomePlaceholderEmpty ? "%biome%" : papiBiomePlaceholder;
                    String biomeNameOfPlayer = new PlaceholderApiHook(biomePlaceholderEmpty).getPlaceholder(player, biomePlaceholder);

                    var matchedBiome = BiomeEvents.getBiomeModels().stream()
                        .filter(x -> x.BiomeId.equalsIgnoreCase(biomeNameOfPlayer))
                        .findFirst().orElse(null);

                    if (matchedBiome == null)
                        continue;

                    // Replace placeholders and execute given command
                    for (String command : matchedBiome.WhileIn.Commands.Commands) {
                        String replacedCommand = new PlaceholderApiHook().getPlaceholder(player, command);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacedCommand);
                    }

                    int randomSoundIndex = 0;

                    if (matchedBiome.WhileIn.Sounds.size() > 1) {
                        randomSoundIndex = ThreadLocalRandom.current().nextInt(0, matchedBiome.WhileIn.Sounds.size() - 1);
                    }

                    SoundModel soundModel = matchedBiome.WhileIn.Sounds.get(randomSoundIndex);

                    // Return only if the permission node is set and player does not have the permission
                    if (!soundModel.Permission.isBlank() && !player.hasPermission(soundModel.Permission))
                        return;

                    int randomIndex = ThreadLocalRandom.current().nextInt(0, soundModel.Chance);

                    // Calculate chance
                    ChanceCalculation calculatedChance = new ChanceCalculation(randomIndex, soundModel.Chance);

                    if (!calculatedChance.matchedIndex())
                        return;

                    Location soundLocation = player.getLocation();

                    // Add random offset if configured
                    if (soundModel.MaxRandomOffset > 0) {
                        double randomOffsetX = ThreadLocalRandom.current()
                            .nextDouble(soundModel.MaxRandomOffset * -1, soundModel.MaxRandomOffset);
                        double randomOffsetZ = ThreadLocalRandom.current()
                                .nextDouble(soundModel.MaxRandomOffset * -1, soundModel.MaxRandomOffset);
                        soundLocation.add(new Vector(randomOffsetX, 0, randomOffsetZ));
                    }

                    if (soundModel.IsServerWide || soundModel.MaxRandomOffset > 0) {
                        player.getWorld().playSound(
                            soundLocation,
                            soundModel.Sound,
                            Parser.parse(soundModel.Category, SoundCategory.AMBIENT),
                            soundModel.Volume,
                            soundModel.Pitch
                        );
                    } else {
                        player.playSound(
                            soundLocation,
                            soundModel.Sound,
                            Parser.parse(soundModel.Category, SoundCategory.AMBIENT),
                            soundModel.Volume,
                            soundModel.Pitch
                        );
                    }
                }
            }
        }.runTaskTimer(plugin, 0, BiomeEvents.getPositionChecksInTicks());
    }
}
