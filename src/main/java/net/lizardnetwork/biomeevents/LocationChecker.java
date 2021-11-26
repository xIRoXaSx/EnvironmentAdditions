package net.lizardnetwork.biomeevents;

import net.lizardnetwork.biomeevents.external.PlaceholderApiHook;
import net.lizardnetwork.biomeevents.helper.ChanceCalculation;
import net.lizardnetwork.biomeevents.helper.Parser;
import net.lizardnetwork.biomeevents.models.ConditionModel;
import net.lizardnetwork.biomeevents.models.SoundModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.WeatherType;
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

    /**
     * Start the BukkitRunnable which will check player positions for biomes to execute commands and sounds
     * depending on the players current location
     * @return <code>BukkitTask</code> - The running BukkitTask
     */
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

                    // Check if conditions meet
                    if (!passedConditionChecks(matchedBiome.WhileIn.Conditions, player))
                        return;

                    // Replace placeholders and execute given command
                    for (String command : matchedBiome.WhileIn.Commands.Commands) {
                        String replacedCommand = new PlaceholderApiHook().getPlaceholder(player, command);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacedCommand);
                    }

                    int randomSoundIndex = 0;

                    if (matchedBiome.WhileIn.Sounds.size() > 1) {
                        randomSoundIndex = ThreadLocalRandom.current().nextInt(0, matchedBiome.WhileIn.Sounds.size());
                    }

                    SoundModel soundModel = matchedBiome.WhileIn.Sounds.get(randomSoundIndex);

                    // Return only if the permission node is set and player does not have the permission
                    if (!soundModel.Permission.isBlank() && !player.hasPermission(soundModel.Permission))
                        return;

                    // Check if conditions meet
                    if (!passedConditionChecks(soundModel.Conditions, player))
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

    /**
     * Check if the target passed its condition checks
     * @param conditions <code>ConditionModel</code> - The ConditionModel for which the checks should be executed
     * @param currentPlayer <code>Player</code> - The Player for which the check should be executed
     * @return <code>Boolean</code> - <code>True</code> if all checks (if enabled) passed or nothing needed to be checked,
     *         otherwise <code>false</code>
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean passedConditionChecks(ConditionModel conditions, Player currentPlayer) {
        if (conditions != null && conditions.getEnableCondition()) {
            WeatherType worldWeatherCondition = currentPlayer.getWorld().isClearWeather() ? WeatherType.CLEAR : WeatherType.DOWNFALL;

            if (!conditions.getWeatherCondition().equals(worldWeatherCondition))
                return false;

            if (conditions.getStartTimeCondition() > currentPlayer.getWorld().getTime())
                return false;

            return conditions.getEndTimeCondition() >= currentPlayer.getWorld().getTime();
        }

        return true;
    }
}
