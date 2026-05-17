package kbtu_oop_project.domain.features.notification;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final String message;
    private final long timestampMillis;
    private boolean isRead;

    public Notification(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Текст уведомления не может быть пустым");
        }
        this.message = message.trim();
        this.timestampMillis = System.currentTimeMillis(); 
        this.isRead = false;
    }

    public Notification(String message, long timestampMillis, boolean isRead) {
        this.message = message;
        this.timestampMillis = timestampMillis;
        this.isRead = isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public String getFormattedTimestamp() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestampMillis), 
                ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        String status = isRead ? "[Прочитано]" : "[НОВОЕ] 🟢";
        return String.format("%s (%s): %s", status, getFormattedTimestamp(), message);
    }

    
    public String getMessage() { 
        return message; 
    }

    public long getTimestampMillis() { 
        return timestampMillis; 
    }

    public boolean isRead() { 
        return isRead; 
    }
}