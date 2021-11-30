package net.lizardnetwork.biomeevents;

import net.lizardnetwork.biomeevents.external.PlaceholderApiHook;
import net.lizardnetwork.biomeevents.helper.ChanceCalculation;
import net.lizardnetwork.biomeevents.helper.Parser;
import net.lizardnetwork.biomeevents.models.BiomeModel;
import net.lizardnetwork.biomeevents.models.ConditionModel;
import net.lizardnetwork.biomeevents.models.ParticleModel;
import net.lizardnetwork.biomeevents.models.SoundModel;
import org.bukkit.*;
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
                        .filter(x -> x.getBiomeId().equalsIgnoreCase(biomeNameOfPlayer))
                        .findFirst().orElse(null);

                    if (matchedBiome == null)
                        continue;

                    // Check if conditions meet
                    if (!passedConditionChecks(matchedBiome.getWhileInBiomeEventModel().Conditions, player))
                        return;

                    // Replace placeholders and execute given command
                    for (String command : matchedBiome.getWhileInBiomeEventModel().Commands.Commands) {
                        String replacedCommand = new PlaceholderApiHook().getPlaceholder(player, command);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacedCommand);
                    }

                    playSound(player, matchedBiome);
                    spawnParticles(player, matchedBiome);
                }
            }
        }.runTaskTimer(plugin, 0, BiomeEvents.getPositionChecksInTicks());
    }

    /**
     * Play a SoundModel sound
     * @param player <code>Player</code> - The current player
     * @param biomeModel <code>BiomeModel</code> - The BiomeModel which matched the players biome
     */
    private void playSound(Player player, BiomeModel biomeModel) {
        int randomSoundIndex = 0;

        if (biomeModel.getWhileInBiomeEventModel().Sounds.size() > 1) {
            randomSoundIndex = ThreadLocalRandom.current()
                .nextInt(0, biomeModel.getWhileInBiomeEventModel().Sounds.size());
        }

        SoundModel soundModel = biomeModel.getWhileInBiomeEventModel().Sounds.get(randomSoundIndex);

        // Return only if the permission node is set and player does not have the permission
        if (!soundModel.getPermission().isBlank() && !player.hasPermission(soundModel.getPermission()))
            return;

        // Check if conditions meet
        if (!passedConditionChecks(soundModel.getConditions(), player))
            return;

        int randomIndex = ThreadLocalRandom.current().nextInt(0, soundModel.getChance());

        // Calculate chance
        ChanceCalculation calculatedChance = new ChanceCalculation(randomIndex, soundModel.getChance());

        if (!calculatedChance.matchedIndex())
            return;

        Location soundLocation = player.getLocation();

        // Add random offset if configured
        if (soundModel.getMaxRandomOffset() > 0) {
            double randomOffsetX = ThreadLocalRandom.current()
                    .nextDouble(soundModel.getMaxRandomOffset() * -1, soundModel.getMaxRandomOffset());
            double randomOffsetZ = ThreadLocalRandom.current()
                    .nextDouble(soundModel.getMaxRandomOffset() * -1, soundModel.getMaxRandomOffset());
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
    private void spawnParticles(Player player, BiomeModel biomeModel) {
        int randomParticleIndex = 0;

        if (biomeModel.getWhileInBiomeEventModel().ParticleModels.size() > 1) {
            randomParticleIndex = ThreadLocalRandom.current()
                .nextInt(0, biomeModel.getWhileInBiomeEventModel().ParticleModels.size());
        }

        ParticleModel particleModel = biomeModel.getWhileInBiomeEventModel().ParticleModels.get(randomParticleIndex);

        // Return only if the permission node is set and player does not have the permission
        if (!particleModel.getPermission().isBlank() && !player.hasPermission(particleModel.getPermission()))
            return;

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
                for (int y = player.getLocation().getBlockY() + finalRadiusInBlocks + 1; y > player.getLocation().getBlockY() - finalRadiusInBlocks; y--) {
                    int randomIndex = ThreadLocalRandom.current().nextInt(0, finalChanceForEachLoop);
                    ChanceCalculation calculatedChance = new ChanceCalculation(randomIndex, finalChanceForEachLoop);

                    if (!calculatedChance.matchedIndex())
                        continue;

                    for (int x = 0; x < finalRadiusInBlocks + 1; x++) {
                        randomIndex = ThreadLocalRandom.current().nextInt(0, finalChanceForEachLoop);
                        calculatedChance = new ChanceCalculation(randomIndex, finalChanceForEachLoop);

                        if (!calculatedChance.matchedIndex())
                            continue;

                        for (int z = 0; z < finalRadiusInBlocks + 1; z++) {
                            // Calculate chance
                            randomIndex = ThreadLocalRandom.current().nextInt(0, finalChanceForEachLoop);
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
