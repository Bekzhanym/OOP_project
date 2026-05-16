package kbtu_oop_project.domain.features.misc;

import java.io.Serializable;

public final class EmployeeMessage implements Serializable {

    public static final String KIND_MESSAGE = "MESSAGE";
    public static final String KIND_COMPLAINT = "COMPLAINT";

    private static final long serialVersionUID = 1L;

    private String fromUserId;
    private String fromEmail;
    private String toEmail;
    private String kind;
    private String body;
    private boolean requiresDeanSignature;
    private long createdAtEpochMillis;

    public EmployeeMessage() {
    }

    public EmployeeMessage(String fromUserId, String fromEmail, String toEmail, String kind,
                           String body, boolean requiresDeanSignature, long createdAtEpochMillis) {
        this.fromUserId = fromUserId;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.kind = kind;
        this.body = body;
        this.requiresDeanSignature = requiresDeanSignature;
        this.createdAtEpochMillis = createdAtEpochMillis;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isRequiresDeanSignature() {
        return requiresDeanSignature;
    }

    public void setRequiresDeanSignature(boolean requiresDeanSignature) {
        this.requiresDeanSignature = requiresDeanSignature;
    }

    public long getCreatedAtEpochMillis() {
        return createdAtEpochMillis;
    }

    public void setCreatedAtEpochMillis(long createdAtEpochMillis) {
        this.createdAtEpochMillis = createdAtEpochMillis;
    }

    @Override
    public String toString() {
        return "[" + kind + "] " + fromEmail + " → " + toEmail + ": " + body;
    }
}
