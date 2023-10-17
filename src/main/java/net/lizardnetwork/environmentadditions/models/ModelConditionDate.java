package net.lizardnetwork.environmentadditions.models;

import java.time.LocalDateTime;
import javax.annotation.Nullable;
import net.lizardnetwork.environmentadditions.Logging;

public class ModelConditionDate {
    private final @Nullable LocalDateTime fromDate;
    private final @Nullable LocalDateTime toDate;
    private final boolean isDisabled;

    public ModelConditionDate(String fromDate, String toDate) {
        this.isDisabled = fromDate.equals("") && toDate.equals("");
        if (this.isDisabled) {
            this.fromDate = null;
            this.toDate = null;
            return;
        }

        LocalDateTime parsed = null;
        try {
            parsed = LocalDateTime.parse(fromDate);
        } catch (Exception e) {
            Logging.warn("DateTime.From cannot be parsed: " + e.getMessage());
        }
        this.fromDate = parsed;

        try {
            parsed = LocalDateTime.parse(toDate);
        } catch (Exception e) {
            parsed = null;
            Logging.warn("DateTime.To cannot be parsed: " + e.getMessage());
        }
        this.toDate = parsed;
    }

    public boolean isBetween() {
        return this.isDisabled || currentTimeMatches();
    }

    private boolean currentTimeMatches() {
        LocalDateTime now = LocalDateTime.now();
        return (toDate == null || now.isBefore(this.toDate))
            && (fromDate == null || now.isAfter(this.fromDate));
    }
}