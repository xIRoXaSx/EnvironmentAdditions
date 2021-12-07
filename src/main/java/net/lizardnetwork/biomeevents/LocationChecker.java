package net.lizardnetwork.biomeevents;

import net.lizardnetwork.biomeevents.external.Placeholder;
import net.lizardnetwork.biomeevents.external.PlaceholderApiHook;
import net.lizardnetwork.biomeevents.helper.ChanceCalculation;
import net.lizardnetwork.biomeevents.helper.Parser;
import net.lizardnetwork.biomeevents.models.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocationChecker {
    private final Plugin plugin;
    private final String papiBiomePlaceholder;
    private final static String biomeFallbackPlaceholder = "%player_biome%";

    public LocationChecker(Plugin plugin, String papiBiomePlaceholder) {
        this.plugin = plugin;
        this.papiBiomePlaceholder = papiBiomePlaceholder;
    }

    /**
     * Start the BukkitRunnable which will check player positions for biomes to execute commands and sounds
     * depending on the players current location
     * @return <code>BukkitTask</code> - The running BukkitTask
     */
    BukkitTask initializeMainTimeDrivenSystem() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                var onlinePlayers = plugin.getServer().getOnlinePlayers();

                for (Player player : onlinePlayers) {
                    boolean biomePlaceholderEmpty = papiBiomePlaceholder == null || papiBiomePlaceholder.isBlank();
                    String biomePlaceholder = biomePlaceholderEmpty ? biomeFallbackPlaceholder : papiBiomePlaceholder;
                    String biomeNameOfPlayer = new PlaceholderApiHook(biomePlaceholderEmpty, biomeFallbackPlaceholder).getPlaceholder(player, biomePlaceholder);

                    var matchedBiome = BiomeEvents.getBiomeModels().stream()
                        .filter(x -> x.getBiomeId().equalsIgnoreCase(biomeNameOfPlayer))
                        .findFirst().orElse(null);

                    if (matchedBiome == null)
                        continue;

                    // Check if conditions meet
                    if (!passedConditionChecks(matchedBiome.getWhileInBiomeEventModel().Conditions, player))
                        return;

                    executeCommand(player, matchedBiome);
                    playSound(player, matchedBiome);
                }
            }
        }.runTaskTimer(plugin, 0, BiomeEvents.getPositionChecksInTicks());
    }

    /**
     * Start the BukkitRunnable which will check player positions for biomes to spawn particles
     * depending on the players current location
     * @return <code>BukkitTask</code> - The running BukkitTask
     */
    BukkitTask initializeParticleTimeDrivenSystem() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                var onlinePlayers = plugin.getServer().getOnlinePlayers();

                for (Player player : onlinePlayers) {
                    boolean biomePlaceholderEmpty = papiBiomePlaceholder == null || papiBiomePlaceholder.isBlank();
                    String biomePlaceholder = biomePlaceholderEmpty ? biomeFallbackPlaceholder : papiBiomePlaceholder;
                    String biomeNameOfPlayer = new PlaceholderApiHook(biomePlaceholderEmpty, biomeFallbackPlaceholder).getPlaceholder(player, biomePlaceholder);

                    var matchedBiome = BiomeEvents.getBiomeModels().stream()
                        .filter(x -> x.getBiomeId().equalsIgnoreCase(biomeNameOfPlayer))
                        .findFirst().orElse(null);

                    if (matchedBiome == null)
                        continue;

                    // Check if conditions meet
                    if (!passedConditionChecks(matchedBiome.getWhileInBiomeEventModel().Conditions, player))
                        return;

                    spawnParticles(player, matchedBiome);
                }
            }
        }.runTaskTimer(plugin, 0, BiomeEvents.getPositionParticleChecksInTicks());
    }

    /**
     * Execute a CommandModel
     * @param player <code>Player</code> - The current player
     * @param biomeModel <code>BiomeModel</code> - The BiomeModel which matched the players biome
     */
    private void executeCommand(Player player, @NotNull BiomeModel biomeModel) {
        if (biomeModel.getWhileInBiomeEventModel().Commands.size() < 1)
            return;

        if (!BiomeEvents.isSingleCommandModelEnabled()) {
            for (CommandModel commandModel : biomeModel.getWhileInBiomeEventModel().Commands) {
                executeCommandFromModel(player, commandModel);
            }

            return;
        }

        int randomCommandIndex = new ChanceCalculation(0, biomeModel.getWhileInBiomeEventModel().Commands.size()).getRandomInteger();
        CommandModel commandModel = biomeModel.getWhileInBiomeEventModel().Commands.get(randomCommandIndex);

        executeCommandFromModel(player, commandModel);
    }

    /**
     * Execute commands from the given CommandModel
     * @param player <code>Player</code> - The current player
     * @param commandModel <code>CommandModel</code> - The CommandModel containing the commands
     */
    private void executeCommandFromModel(Player player, CommandModel commandModel) {
        // Check if conditions meet
        if (!passedConditionChecks(commandModel.getConditions(), player))
            return;

        Placeholder placeholder;
        List<String> commands = new ArrayList<>();

        if (commandModel.isRandom()) {
            int randomCommandIndex = new ChanceCalculation(0, commandModel.getCommandList().size()).getRandomInteger();
            placeholder = new Placeholder(commandModel.getCommandList().get(randomCommandIndex));
            commands.add(placeholder.replaceFromPlaceholderApi(player));
        } else {
            commands.addAll(commandModel.getCommandList().stream().map(x -> new Placeholder(x).replaceFromPlaceholderApi(player)).toList());
        }

        for (String command : commands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    /**
     * Play a SoundModel sound
     * @param player <code>Player</code> - The current player
     * @param biomeModel <code>BiomeModel</code> - The BiomeModel which matched the players biome
     */
    private void playSound(Player player, @NotNull BiomeModel biomeModel) {
        int randomSoundIndex = new ChanceCalculation(0, biomeModel.getWhileInBiomeEventModel().Sounds.size()).getRandomInteger();
        SoundModel soundModel = biomeModel.getWhileInBiomeEventModel().Sounds.get(randomSoundIndex);

        // Check if conditions meet
        if (!passedConditionChecks(soundModel.getConditions(), player))
            return;

        // Calculate chance
        int randomIndex = new ChanceCalculation(0, soundModel.getChance()).getRandomInteger();
        ChanceCalculation calculatedChance = new ChanceCalculation(randomIndex, soundModel.getChance());

        if (!calculatedChance.matchedIndex())
            return;

        Location soundLocation = player.getLocation();

        // Add random offset if configured
        if (soundModel.getMaxRandomOffset() > 0) {
            double randomOffsetX = new ChanceCalculation(-soundModel.getMaxRandomOffset() - 1, soundModel.getMaxRandomOffset() + 1).getRandomDouble();
            double randomOffsetZ = new ChanceCalculation(-soundModel.getMaxRandomOffset() - 1, soundModel.getMaxRandomOffset() + 1).getRandomDouble();
            soundLocation.add(new Vector(randomOffsetX, 0, randomOffsetZ));
        }

        if (soundModel.isServerWide() || soundModel.getMaxRandomOffset() > 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.getWorld().playSound(
                soundLocation,
                soundModel.getSound(),
                Parser.parse(soundModel.getCategory(), SoundCategory.AMBIENT),
                soundModel.getVolume(),
                soundModel.getPitch()
            ), 0);
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (player.isOnline())
                    player.playSound(
                        soundLocation,
                        soundModel.getSound(),
                        Parser.parse(soundModel.getCategory(), SoundCategory.AMBIENT),
                        soundModel.getVolume(),
                        soundModel.getPitch()
                    );
            }, 0);
        }
    }

    /**
     * Spawn the corresponding particles to the players biome
     * @param player <code>Player</code> - The current player
     * @param biomeModel <code>BiomeModel</code> - The BiomeModel which matched the players biome
     */
    private void spawnParticles(Player player, @NotNull BiomeModel biomeModel) {
        int randomParticleIndex = 0;

        if (biomeModel.getWhileInBiomeEventModel().ParticleModels.size() > 1) {
            randomParticleIndex = new ChanceCalculation(0, biomeModel.getWhileInBiomeEventModel().ParticleModels.size()).getRandomInteger();
        }

        ParticleModel particleModel = biomeModel.getWhileInBiomeEventModel().ParticleModels.get(randomParticleIndex);

        // Check if conditions meet
        if (!passedConditionChecks(particleModel.getConditions(), player))
            return;

        Particle particle = Parser.parse(particleModel.getParticle(), (Particle) null);

        if (particle == null)
            return;

        Particle.DustOptions dustOptions = null;
        double fromX = player.getLocation().getX();
        double fromY = player.getLocation().getY();
        double fromZ = player.getLocation().getZ();
        int radiusInBlocks = 0;
        int chanceForEachLoop = 1;

        if (particleModel.getParticleAnimationModel() != null) {
            fromX += particleModel.getParticleAnimationModel().getRelativeOffsetX();
            fromY += particleModel.getParticleAnimationModel().getRelativeOffsetY();
            fromZ += particleModel.getParticleAnimationModel().getRelativeOffsetZ();

            if (particleModel.getParticleAnimationModel().getViewDirectionDistance() != 0) {
                double viewDirectionDistance = particleModel.getParticleAnimationModel().getViewDirectionDistance();
                Location eyeLocation = player.getEyeLocation();
                Vector nv = eyeLocation.getDirection().normalize();

                fromX = viewDirectionDistance * nv.getX() + eyeLocation.getX();
                fromZ = viewDirectionDistance * nv.getZ() + eyeLocation.getZ();
            }

            if (particleModel.getParticleAnimationModel().getLoopOption() != null) {
                radiusInBlocks = particleModel.getParticleAnimationModel().getLoopOption().getRadiusInBlocks();
                chanceForEachLoop = particleModel.getParticleAnimationModel().getLoopOption().getChanceForEachLoop();
            }
        }

        if (particle == Particle.REDSTONE) {
            int particleSize = particleModel.getRedstoneSize();
            dustOptions = new Particle.DustOptions(Color.fromRGB(255,255,255), particleSize);

            if (particleModel.getRedstoneHexColor() != null && !particleModel.getRedstoneHexColor().isBlank()
                    && !particleModel.getRedstoneHexColor().equals("null")) {
                java.awt.Color tempColor = java.awt.Color.decode(particleModel.getRedstoneHexColor());
                dustOptions = new Particle.DustOptions(Color.fromRGB(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue()), particleSize);
            }
        }

        double finalFromX = fromX;
        double finalFromY = fromY;
        double finalFromZ = fromZ;
        int finalRadiusInBlocks = radiusInBlocks;
        int finalChanceForEachLoop = chanceForEachLoop;
        Particle.DustOptions finalDustOptions = dustOptions;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (finalRadiusInBlocks > 0) {
                if (particleModel.getParticleAnimationModel().getLoopOption().getVersion() != 1) {
                    for (int i = 0; i < finalChanceForEachLoop + 1; i++) {
                        double randomX = new ChanceCalculation((double) -finalRadiusInBlocks - 1, (double) finalRadiusInBlocks + 1).getRandomDouble();
                        double randomY = new ChanceCalculation((double) -finalRadiusInBlocks - 1, (double) finalRadiusInBlocks + 1).getRandomDouble();
                        double randomZ = new ChanceCalculation((double) -finalRadiusInBlocks - 1, (double) finalRadiusInBlocks + 1).getRandomDouble();

                        player.spawnParticle(
                            particle,
                            finalFromX + randomX,
                            finalFromY + randomY,
                            finalFromZ + randomZ,
                            particleModel.getParticleCount(),
                            finalDustOptions
                        );
                    }
                } else {
                    // Version 1 spawning
                    for (int y = player.getLocation().getBlockY() + finalRadiusInBlocks + 1; y > player.getLocation().getBlockY() - finalRadiusInBlocks; y--) {
                        int randomIndex = new ChanceCalculation(0,finalChanceForEachLoop).getRandomInteger();
                        ChanceCalculation calculatedChance = new ChanceCalculation(randomIndex, finalChanceForEachLoop);

                        if (!calculatedChance.matchedIndex())
                            continue;

                        for (int x = 0; x < finalRadiusInBlocks + 1; x++) {
                            randomIndex = new ChanceCalculation(0, finalChanceForEachLoop).getRandomInteger();
                            calculatedChance = new ChanceCalculation(randomIndex, finalChanceForEachLoop);

                            if (!calculatedChance.matchedIndex())
                                continue;

                            for (int z = 0; z < finalRadiusInBlocks + 1; z++) {
                                // Calculate chance
                                randomIndex = new ChanceCalculation(0, finalChanceForEachLoop).getRandomInteger();
                                calculatedChance = new ChanceCalculation(randomIndex, finalChanceForEachLoop);

                                if (!calculatedChance.matchedIndex())
                                    continue;

                                player.spawnParticle(
                                    particle,
                                    finalFromX + x,
                                    y,
                                    finalFromZ + z,
                                    particleModel.getParticleCount(),
                                    finalDustOptions
                                );

                                player.spawnParticle(
                                    particle,
                                    finalFromX + x,
                                    y,
                                    finalFromZ - z,
                                    particleModel.getParticleCount(),
                                    finalDustOptions
                                );

                                player.spawnParticle(
                                    particle,
                                    finalFromX - x,
                                    y,
                                    finalFromZ + z,
                                    particleModel.getParticleCount(),
                                    finalDustOptions
                                );

                                player.spawnParticle(
                                    particle,
                                    finalFromX - x,
                                    y,
                                    finalFromZ - z,
                                    particleModel.getParticleCount(),
                                    finalDustOptions
                                );
                            }
                        }
                    }
                }

                return;
            }

            player.spawnParticle(
                particle,
                finalFromX,
                finalFromY,
                finalFromZ,
                particleModel.getParticleCount(),
                finalDustOptions
            );
        }, 0);
    }

    /**
     * Check if the target passed its condition checks
     * @param conditions <code>ConditionModel</code> - The ConditionModel for which the checks should be executed
     * @param currentPlayer <code>Player</code> - The Player for which the check should be executed
     * @return <code>Boolean</code> - <code>True</code> if all checks (if enabled) passed or nothing needed to be checked,
     *         otherwise <code>false</code>
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean passedConditionChecks(ConditionModel conditions, Player currentPlayer) {
        if (conditions != null && conditions.isEnabled()) {
            WeatherType worldWeatherCondition = currentPlayer.getWorld().isClearWeather() ? WeatherType.CLEAR : WeatherType.DOWNFALL;

            if (!Objects.equals(conditions.getPermission(), "null") && !conditions.getPermission().isBlank()
                    && !currentPlayer.hasPermission(conditions.getPermission()))
                return false;

            if (conditions.getWeather() != null && !conditions.getWeather().equals(worldWeatherCondition))
                return false;

            if (conditions.getFromTimeInTicks() > currentPlayer.getWorld().getTime())
                return false;

            return conditions.getUntilTimeInTicks() >= currentPlayer.getWorld().getTime();
        }

        return true;
    }
}
