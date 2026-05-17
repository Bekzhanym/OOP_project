package kbtu_oop_project.domain.features.user;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class RecommendationLetter implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String text;
    private final LocalDate date;
    private final String studentEmail; 
    
    private final String authorId;
    private final String authorFullName;

    private RecommendationLetter() {
        this.text = "Пустая рекомендация.";
        this.date = LocalDate.now();
        this.studentEmail = "guest@kbtu.kz";
        this.authorId = "SYSTEM";
        this.authorFullName = "System Office";
    }

    public RecommendationLetter(Teacher author, String studentEmail, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Текст рекомендательного письма не может быть пустым.");
        }
        if (studentEmail == null || studentEmail.isBlank()) {
            throw new IllegalArgumentException("Email целевого студента обязателен.");
        }
        Objects.requireNonNull(author, "Автор (Teacher) обязателен для верификации письма.");

        this.text = text.trim();
        this.studentEmail = studentEmail.trim().toLowerCase();
        
        this.authorId = author.getId();
        this.authorFullName = author.getFullName();
        
        this.date = LocalDate.now(); 
    }

    @Override
    public String toString() {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return String.format(
                "==================================================\n" +
                "📜 ОФИЦИАЛЬНОЕ РЕКОМЕНДАТЕЛЬНОЕ ПИСЬМО\n" +
                "От кого: %s (ID: %s)\n" +
                "Кому (Студент): %s\n" +
                "Дата выдачи: %s\n" +
                "--------------------------------------------------\n" +
                "Текст: %s\n" +
                "==================================================",
                authorFullName, authorId, studentEmail, formattedDate, text
        );
    }

    
    public String getText() { return text; }
    public LocalDate getDate() { return date; }
    public String getStudentEmail() { return studentEmail; }
    public String getAuthorId() { return authorId; }
    public String getAuthorFullName() { return authorFullName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationLetter that = (RecommendationLetter) o;
        return Objects.equals(text, that.text) && 
               Objects.equals(date, that.date) && 
               Objects.equals(studentEmail, that.studentEmail) && 
               Objects.equals(authorId, that.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, date, studentEmail, authorId);
    }
}