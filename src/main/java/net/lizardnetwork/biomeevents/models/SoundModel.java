package net.lizardnetwork.biomeevents.models;

public class SoundModel {
    public int Chance = 1;
    public String Sound;
    public String Category = "Ambient";
    public Float Volume = 1.0f;
    public Float Pitch = 1.0f;
    public boolean IsServerWide = false;
    public String Permission = "";
    public double MaxRandomOffset = -1d;
    public ConditionModel Conditions = new ConditionModel();
}
