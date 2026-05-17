package kbtu_oop_project.domain.features.misc;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class PendingCourseRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String studentEmail;
    private String courseCode;
    private long submittedAtEpochMillis;

    public PendingCourseRegistration() {
        this.submittedAtEpochMillis = System.currentTimeMillis();
    }

    public PendingCourseRegistration(String studentEmail, String courseCode) {
        if (studentEmail == null || studentEmail.isBlank()) throw new IllegalArgumentException("Email студента обязателен");
        if (courseCode == null || courseCode.isBlank()) throw new IllegalArgumentException("Код курса обязателен");
        
        this.studentEmail = studentEmail;
        this.courseCode = courseCode;
        this.submittedAtEpochMillis = System.currentTimeMillis();
    }

    public PendingCourseRegistration(String studentEmail, String courseCode, long submittedAtEpochMillis) {
        this.studentEmail = studentEmail;
        this.courseCode = courseCode;
        this.submittedAtEpochMillis = submittedAtEpochMillis;
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
        return String.format("Заявка: Студент [%s] претендует на курс [%s] (Подано: %s)", 
                studentEmail, courseCode, getFormattedSubmissionTime());
    }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public long getSubmittedAtEpochMillis() { return submittedAtEpochMillis; }
    public void setSubmittedAtEpochMillis(long submittedAtEpochMillis) { this.submittedAtEpochMillis = submittedAtEpochMillis; }
}