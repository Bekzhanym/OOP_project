package kbtu_oop_project.domain.features.notification;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    private String message;
    private LocalDate date;
    private boolean read;

    public Notification(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Текст уведомления не может быть пустым");
        }
        this.message = message;
        this.date = LocalDate.now();
        this.read = false;
    }

    public Notification(String message, LocalDate date, boolean read) {
        this.message = message;
        this.date = date != null ? date : LocalDate.now();
        this.read = read;
    }

    @Override
    public String toString() {
        String status = read ? "[Прочитано]" : "[НОВОЕ] *";
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s (%s): %s", status, formattedDate, message);
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}