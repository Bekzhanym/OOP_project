package kbtu_oop_project.domain.features.misc;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private LocalDateTime timestamp;
    private String action;
    private String userId;

    public Log() {
    }

    public Log(String userId, String action) {
        this.userId = userId;
        this.action = action;
        this.timestamp = LocalDateTime.now(); // Автоматическая фиксация времени
    }

    public String getFormattedTimestamp() {
        if (timestamp == null) return "N/A";
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("[%s] [User ID: %s] — Действие: %s", 
                getFormattedTimestamp(), userId, action);
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}