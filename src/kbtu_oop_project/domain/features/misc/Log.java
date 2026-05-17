package kbtu_oop_project.domain.features.misc;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Log implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final long timestampMillis;
    private final String action;
    private final String userId;

    public Log(String userId, String action) {
        if (userId == null || userId.isBlank()) {
            this.userId = "SYSTEM/ANONYMOUS";
        } else {
            this.userId = userId.trim();
        }
        
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Описание действия для лога не может быть пустым.");
        }
        
        this.action = action.trim();
        this.timestampMillis = System.currentTimeMillis(); 
    }

    public LocalDateTime getTimestamp() {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestampMillis), 
                ZoneId.systemDefault()
        );
    }

    public String getFormattedTimestamp() {
        return getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("[%s] [Инициатор ID: %s] — Действие: %s", 
                getFormattedTimestamp(), userId, action);
    }

    public long getTimestampMillis() { 
        return timestampMillis; 
    }

    public String getAction() { 
        return action; 
    }

    public String getUserId() { 
        return userId; 
    }
}