# EnvironmentAdditions
## About
EnvironmentAdditions is a plugin designed for [SpigotMC](https://www.spigotmc.org/) (and related) servers to enhance
player experiences.  
If you like to add some **sounds** and **particles** to specific biomes, look no further!  
**Commands** can also be executed for / as the player while they are in the configured biomes.  
With this plugin you are finally able to extend the look and feel of vanilla **or even custom biomes** to your liking!  
If you are using a custom resource pack for the server with lots of custom sounds, you can of course use them as well.    
A small example here:
![EnvironmentAdditions](https://user-images.githubusercontent.com/38859398/199825810-c9c201c6-7706-45c5-a677-c3ae95b2999a.gif)
\[Caption: Particles are being spawned to simulate snow] 

## Commands
The current list of implemented commands:

| Command                      | Permission                   | Description                                                                                         |
|------------------------------|------------------------------|-----------------------------------------------------------------------------------------------------|
| /ea benchmark \[iter] \[min] | `environmentadditions.admin` | Benchmark your current configuration(s) and simulate *iter* number of players for *min* minutes.    |
| /ea help                     | `environmentadditions.admin` | Print the help message. Will also provide information about command aliases and clickable messages. |
| /ea reload                   | `environmentadditions.admin` | Reload the configurations.                                                                          |

If no arguments have been passed to `/ea benchmark`, the benchmark will default to **50** simulated players and a timespan of **1** minute. 
While the benchmark is running you can measure your Ticks per second (TPS) with another utility (e.g. `essentials` or `timings`).

## Permissions
As described above, for **reloading**, **benchmarking** and printing the **help** message,
the permission `environmentadditions.admin` is required.  
Casual players **DO NOT NEED** any permission whatsoever and should not be given the stated permission!  
BUT: You are able to set permissions for each condition (`conditions.yml`), to only allow certain actions
(particles, sounds, commands) for individual players / player groups. If a player does **not** have the required
permission, the action won't execute.

## Configuration
You will get 6 configuration files for their dedicated use.  
This has one huge advantage: You don't need to scroll / search the whole day for modifications 😉

```
../plugins/EnvironmentAdditions/
    ├ biomes.yml      => Contains groups of biomes in which events should be executed.
    ├ commands.yml    => Contains groups of commands, condition references, execution type, ...
    ├ conditions.yml  => Contains groups of conditions, e.g. time, weather, permission, ...
    ├ config.yml      => The config with its core settings and all referenced groups. 
    ├ particles.yml   => Contains groups of particles, animations, ...
    └ sounds.yml      => Contains groups of sounds, sound settings, ...
```
The default configs can be found in the [resources](src/main/resources) directory.

### Useful links:
- Biomes: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Biome.html
- Particles: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html
- Sounds: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
- Sound categories: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/SoundCategory.html