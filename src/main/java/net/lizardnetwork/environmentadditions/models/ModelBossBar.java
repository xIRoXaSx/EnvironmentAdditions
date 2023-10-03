package net.lizardnetwork.environmentadditions.models;

import net.lizardnetwork.environmentadditions.enums.EBossBarColor;
import net.lizardnetwork.environmentadditions.enums.EBossBarStyle;

public class ModelBossBar {
    private final String title;
    private final EBossBarColor color;
    private final EBossBarStyle style;

    public ModelBossBar(String title, EBossBarColor color, EBossBarStyle style) {
        this.title = title;
        this.color = color;
        this.style = style;
    }

    public String getTitle() {
        return this.title;
    }

    public EBossBarColor getColor() {
        return this.color;
    }

    public EBossBarStyle getStyle() {
        return this.style;
    }
}
