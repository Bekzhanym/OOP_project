package kbtu_oop_project.domain.features.misc;

import java.io.Serializable;

public final class PendingCourseRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String studentEmail;
    private String courseCode;
    private long submittedAtEpochMillis;

    public PendingCourseRegistration() {
    }

    public PendingCourseRegistration(String studentEmail, String courseCode, long submittedAtEpochMillis) {
        this.studentEmail = studentEmail;
        this.courseCode = courseCode;
        this.submittedAtEpochMillis = submittedAtEpochMillis;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public long getSubmittedAtEpochMillis() {
        return submittedAtEpochMillis;
    }

    public void setSubmittedAtEpochMillis(long submittedAtEpochMillis) {
        this.submittedAtEpochMillis = submittedAtEpochMillis;
    }

    @Override
    public String toString() {
        return studentEmail + " → " + courseCode + " @ " + submittedAtEpochMillis;
    }
}
