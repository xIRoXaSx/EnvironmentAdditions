package net.lizardnetwork.environmentadditions;

import net.lizardnetwork.environmentadditions.enums.Dependency;

public class State {
    private Dependency dependency;

    void updateDependency(Dependency value) {
        this.dependency = value;
    }

    public Dependency getDependency() {
        return dependency;
    }
}
