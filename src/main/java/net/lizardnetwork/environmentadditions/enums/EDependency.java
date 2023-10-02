package net.lizardnetwork.environmentadditions.enums;

import java.util.ArrayList;
import java.util.List;

public enum EDependency {
    None(0),
    PlaceholderAPI(1),
    WordGuard(2);

    private final int id;

    EDependency(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    public static List<EDependency> parse(int id) {
        if (id == 0) {
            return List.of(EDependency.None);
        }

        List<EDependency> deps = new ArrayList<>();
        if ((id & EDependency.PlaceholderAPI.getValue()) > 0) {
            deps.add(EDependency.PlaceholderAPI);
        }
        if ((id & EDependency.WordGuard.getValue()) > 0) {
            deps.add(EDependency.WordGuard);
        }
        return deps;
    }
}
