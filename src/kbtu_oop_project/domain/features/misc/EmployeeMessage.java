package kbtu_oop_project.domain.features.misc;

import kbtu_oop_project.domain.value.MessageKind; 

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class EmployeeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fromUserId;
    private String fromEmail;
    private String toEmail;
    private MessageKind kind; 
    private String body;
    private boolean requiresDeanSignature;
    private boolean isSignedByDean = false; 
    private long createdAtEpochMillis;

    public EmployeeMessage() {
        this.createdAtEpochMillis = System.currentTimeMillis();
    }

    public EmployeeMessage(String fromUserId, String fromEmail, String toEmail, MessageKind kind,
                           String body, boolean requiresDeanSignature) {
        
        if (body == null || body.isBlank()) throw new IllegalArgumentException("Тело сообщения не может быть пустым");
        if (fromEmail == null || toEmail == null) throw new IllegalArgumentException("Email отправителя и получателя обязательны");
        
        this.fromUserId = fromUserId;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.kind = Objects.requireNonNull(kind, "Тип сообщения не может быть null");
        this.body = body;
        this.requiresDeanSignature = requiresDeanSignature;
        this.createdAtEpochMillis = System.currentTimeMillis(); // Задаем время создания автоматически
    }

    public String getFormattedCreationTime() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(createdAtEpochMillis), 
                ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public boolean isSignedByDean() {
        return isSignedByDean;
    }

    public void setSignedByDean(boolean signedByDean) {
        isSignedByDean = signedByDean;
    }

    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }

    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }

    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }

    public MessageKind getKind() { return kind; }
    public void setKind(MessageKind kind) { this.kind = kind; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public boolean isRequiresDeanSignature() { return requiresDeanSignature; }
    public void setRequiresDeanSignature(boolean requiresDeanSignature) { this.requiresDeanSignature = requiresDeanSignature; }

    public long getCreatedAtEpochMillis() { return createdAtEpochMillis; }
    public void setCreatedAtEpochMillis(long createdAtEpochMillis) { this.createdAtEpochMillis = createdAtEpochMillis; }

    @Override
    public String toString() {
        String signStatus = requiresDeanSignature 
                ? (isSignedByDean ? " [ПОДПИСАНО ДЕКАНОМ]" : " [ТРЕБУЕТСЯ ПОДПИСЬ ДЕКАНА]") 
                : "";
        
        return String.format("[%s] %s | %s → %s\nТекст: %s%s", 
                kind, getFormattedCreationTime(), fromEmail, toEmail, body, signStatus);
    }
}