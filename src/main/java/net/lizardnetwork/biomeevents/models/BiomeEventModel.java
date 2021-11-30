package net.lizardnetwork.biomeevents.models;

import java.util.ArrayList;
import java.util.List;

public class BiomeEventModel {
    public ConditionModel Conditions = new ConditionModel();
    public CommandModel Commands = new CommandModel();
    public List<ParticleModel> ParticleModels = new ArrayList<>();
    public List<SoundModel> Sounds = new ArrayList<>();
}
