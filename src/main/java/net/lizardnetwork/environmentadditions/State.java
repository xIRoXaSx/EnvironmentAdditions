package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.enums.Dependency;
import net.lizardnetwork.environmentadditions.models.ModelBiomeEvent;

public class State {
    private Config config;
    private Dependency dependency;
    private ModelBiomeEvent[] biomeEvents;

    public void setConfig() {
        config = new Config();
        biomeEvents = config.linkConfigs();
    }

    void setDependency(Dependency value) {
        this.dependency = value;
    }

    void setBiomeEvents(ModelBiomeEvent[] biomeEvents) {
        this.biomeEvents = biomeEvents;
    }

    public Config getConfig() {
        return config;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public ModelBiomeEvent[] getBiomeEvents() {
        return biomeEvents;
    }
}
