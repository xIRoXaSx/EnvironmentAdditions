package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.enums.Dependency;
import net.lizardnetwork.environmentadditions.models.ModelBiomeEvent;

public class State {
    private Dependency dependency;
    private ModelBiomeEvent[] biomeEvents;

    void setDependency(Dependency value) {
        this.dependency = value;
    }

    void setBiomeEvents(ModelBiomeEvent[] biomeEvents) {
        this.biomeEvents = biomeEvents;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public ModelBiomeEvent[] getBiomeEvents() {
        return biomeEvents;
    }
}
