package kbtu_oop_project.domain.features.misc;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class PendingCourseRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String studentEmail;
    private final String courseCode;
    private final long submittedAtEpochMillis;

    private PendingCourseRegistration() {
        this.studentEmail = "guest@kbtu.kz";
        this.courseCode = "STUB000";
        this.submittedAtEpochMillis = System.currentTimeMillis();
    }

    public PendingCourseRegistration(String studentEmail, String courseCode) {
        if (studentEmail == null || studentEmail.isBlank()) {
            throw new IllegalArgumentException("Email студента обязателен для формирования заявки.");
        }
        
        String cleanedEmail = studentEmail.trim().toLowerCase();
        if (!cleanedEmail.endsWith("@kbtu.kz") || !cleanedEmail.contains("@")) {
            throw new IllegalArgumentException("Некорректный формат почты. Регистрация доступна только для корпоративных аккаунтов @kbtu.kz");
        }
        
        if (courseCode == null || courseCode.isBlank()) {
            throw new IllegalArgumentException("Код дисциплины обязателен для регистрации.");
        }
        
        this.studentEmail = cleanedEmail;
        this.courseCode = courseCode.trim().toUpperCase(); 
        this.submittedAtEpochMillis = System.currentTimeMillis(); 
    }

    public String getFormattedSubmissionTime() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(submittedAtEpochMillis), 
                ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("[Заявка на регистрацию] %s → %s (%s)", 
                studentEmail, courseCode, getFormattedSubmissionTime());
    }

    public String getStudentEmail() { return studentEmail; }
    public String getCourseCode() { return courseCode; }
    public long getSubmittedAtEpochMillis() { return submittedAtEpochMillis; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingCourseRegistration that = (PendingCourseRegistration) o;
        
        return Objects.equals(studentEmail, that.studentEmail) &&
               Objects.equals(courseCode, that.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentEmail, courseCode);
    }
}