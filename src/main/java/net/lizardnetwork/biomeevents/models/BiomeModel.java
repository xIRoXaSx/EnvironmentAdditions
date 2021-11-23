package net.lizardnetwork.biomeevents.models;

import net.lizardnetwork.biomeevents.helper.Parser;
import org.bukkit.SoundCategory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BiomeModel {
    public String BiomeId;
    public BiomeEventModel WhileIn = new BiomeEventModel();
    private final Map<String, Object> valueMap;

    public BiomeModel(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }

    /**
     * Load values from a map into a new BiomeModel instance
     * @return BiomeModel
     */
    public BiomeModel loadBiomeModel() {
        BiomeId = String.valueOf(valueMap.get("BiomeId"));
        WhileIn.Commands.Commands = (List<String>) valueMap.getOrDefault("WhileIn.Commands", new ArrayList<>());
        List<Map<String, Object>> whileInSounds = (List<Map<String, Object>>) valueMap.getOrDefault("WhileIn.Sounds", List.of(Collections.emptyMap()));
        SoundModel whileInSound = new SoundModel();

        for (Map<String, Object> soundModelMap : whileInSounds) {
            whileInSound.Chance = Parser.parse(soundModelMap.get("Chance").toString(), 1);
            whileInSound.Sound = soundModelMap.get("Sound").toString();
            whileInSound.Category = Parser.parse(soundModelMap.get("Category").toString(), SoundCategory.AMBIENT).toString();
            whileInSound.Volume = Parser.parse(soundModelMap.get("Volume").toString(), 0.5f);
            whileInSound.Pitch = Parser.parse(soundModelMap.get("Pitch").toString(), 0.5f);
            whileInSound.IsServerWide = Parser.parse(soundModelMap.get("IsServerWide").toString(), false);
            whileInSound.Permission = soundModelMap.get("Permission").toString();
            whileInSound.MaxRandomOffset =
                // Special case of null handling since it is not needed and can be left out by the user
                soundModelMap.get("MaxRandomOffset") == null ? -1f : Parser.parse(String.valueOf(soundModelMap.get("MaxRandomOffset")), -1f);
        }

        WhileIn.Sounds = List.of(whileInSound);
        return this;
    }
}
