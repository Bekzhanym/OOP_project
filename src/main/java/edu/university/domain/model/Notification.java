package edu.university.domain.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    private String message;
    private LocalDate date;
    private boolean read;

    public Notification(String message, LocalDate date, boolean read) {
        this.message = message;
        this.date = date;
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
