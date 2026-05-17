package kbtu_oop_project.domain.features.user;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class RecommendationLetter implements Serializable {

    private static final long serialVersionUID = 1L;

    private String text;
    private LocalDate date;
    private Teacher author;
    private String studentEmail; 

    public RecommendationLetter() {
        this.date = LocalDate.now();
    }

    public RecommendationLetter(Teacher author, String studentEmail, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Текст рекомендательного письма не может быть пустым");
        }
        this.author = Objects.requireNonNull(author, "Автор (Teacher) обязателен");
        this.studentEmail = Objects.requireNonNull(studentEmail, "Email студента обязателен");
        this.text = text.trim();
        this.date = LocalDate.now(); 
    }

    @Override
    public String toString() {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String authorName = (author != null) ? author.getFullName() : "Unknown Teacher";
        
        return String.format(
                "==================================================\n" +
                "РЕКОМЕНДАТЕЛЬНОЕ ПИСЬМО от %s\n" +
                "Дата выдачи: %s | Для студента: %s\n" +
                "--------------------------------------------------\n" +
                "Текст: %s\n" +
                "==================================================",
                authorName, formattedDate, studentEmail, text
        );
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Teacher getAuthor() { return author; }
    public void setAuthor(Teacher author) { this.author = author; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
}