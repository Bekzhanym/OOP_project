package edu.university.domain.model;

import java.io.Serializable;
import java.time.LocalDate;

public class RecommendationLetter implements Serializable {

    private static final long serialVersionUID = 1L;
    private String text;
    private LocalDate date;
    private Teacher author;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Teacher getAuthor() {
        return author;
    }

    public void setAuthor(Teacher author) {
        this.author = author;
    }
}
