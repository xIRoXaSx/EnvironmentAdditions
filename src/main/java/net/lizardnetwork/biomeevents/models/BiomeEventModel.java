package net.lizardnetwork.biomeevents.models;

import java.util.ArrayList;
import java.util.List;

public class BiomeEventModel {
    public ConditionModel Conditions = new ConditionModel();
    public List<CommandModel> Commands = new ArrayList<>();
    public List<ParticleModel> ParticleModels = new ArrayList<>();
    public List<SoundModel> Sounds = new ArrayList<>();
}
