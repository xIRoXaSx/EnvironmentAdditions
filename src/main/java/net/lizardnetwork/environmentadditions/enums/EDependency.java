package net.lizardnetwork.environmentadditions.enums;

import java.util.ArrayList;
import java.util.List;

public enum EDependency {
    None(0),
    PlaceholderAPI(1),
    WorldGuard(2),
    MythicMobs(4);

    private final int id;

    EDependency(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    public static List<EDependency> parse(int id) {
        if (id == 0) {
            return List.of(None);
        }

        List<EDependency> deps = new ArrayList<>();
        if ((id & PlaceholderAPI.getValue()) > 0) {
            deps.add(PlaceholderAPI);
        }
        if ((id & WorldGuard.getValue()) > 0) {
            deps.add(WorldGuard);
        }
        if ((id & MythicMobs.getValue()) > 0) {
            deps.add(MythicMobs);
        }
        return deps;
    }
}
