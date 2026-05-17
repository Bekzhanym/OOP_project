package kbtu_oop_project.domain.features.misc;

import kbtu_oop_project.domain.value.MessageKind;
import kbtu_oop_project.domain.features.user.User; 

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class EmployeeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String fromUserId;
    private final String fromEmail;
    private final String toEmail;
    private final long createdAtEpochMillis;
    
    private MessageKind kind; 
    private String body;
    
    private final boolean requiresDeanSignature;
    private boolean isSignedByDean = false; 

    public EmployeeMessage() {
        this.fromUserId = "UNKNOWN";
        this.fromEmail = "unknown@kbtu.kz";
        this.toEmail = "unknown@kbtu.kz";
        this.kind = MessageKind.MESSAGE;
        this.body = "Empty message";
        this.requiresDeanSignature = false;
        this.createdAtEpochMillis = System.currentTimeMillis();
    }

    public EmployeeMessage(String fromUserId, String fromEmail, String toEmail, MessageKind kind,
                           String body, boolean requiresDeanSignature) {
        
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Тело сообщения не может быть пустым");
        }
        if (fromEmail == null || toEmail == null) {
            throw new IllegalArgumentException("Email отправителя и получателя обязательны");
        }
        
        this.fromUserId = fromUserId;
        this.fromEmail = fromEmail.trim();
        this.toEmail = toEmail.trim();
        this.kind = Objects.requireNonNull(kind, "Тип сообщения не может быть null");
        this.body = body.trim();
        this.requiresDeanSignature = requiresDeanSignature;
        this.createdAtEpochMillis = System.currentTimeMillis();
    }

    public String getFormattedCreationTime() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(createdAtEpochMillis), 
                ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void sign(User currentUser) {
        if (!this.requiresDeanSignature) {
            throw new IllegalStateException("Это сообщение является информационным и не требует официальной подписи декана.");
        }
        
        if (currentUser == null) {
            throw new IllegalArgumentException("Объект пользователя не может быть null при подписании.");
        }

        boolean isDean = currentUser.getClass().getSimpleName().equalsIgnoreCase("Dean")
                || "DEAN".equalsIgnoreCase(currentUser.getUserRole());

        if (isDean) {
            this.isSignedByDean = true;
        } else {
            throw new SecurityException(String.format(
                    "Критическая ошибка безопасности! Пользователь %s %s (ID: %s) " +
                    "пытается подписать документ без полномочий Декана!", 
                    currentUser.getFirstName(), currentUser.getLastName(), currentUser.getId()));
        }
    }

    public boolean isSignedByDean() {
        return isSignedByDean;
    }

    public boolean isRequiresDeanSignature() { 
        return requiresDeanSignature; 
    }

    
    public String getFromUserId() { return fromUserId; }
    public String getFromEmail() { return fromEmail; }
    public String getToEmail() { return toEmail; }
    public long getCreatedAtEpochMillis() { return createdAtEpochMillis; }

    public MessageKind getKind() { return kind; }
    public void setKind(MessageKind kind) { this.kind = kind; }

    public String getBody() { return body; }
    public void setBody(String body) { 
        if (body == null || body.isBlank()) throw new IllegalArgumentException("Тело сообщения не может быть пустым");
        this.body = body.trim(); 
    }

    @Override
    public String toString() {
        String signStatus = requiresDeanSignature 
                ? (isSignedByDean ? " [✅ ПОДПИСАНО ДЕКАНОМ]" : " [❌ ТРЕБУЕТСЯ ПОДПИСЬ ДЕКАНА]") 
                : "";
        
        return String.format("[%s] %s | %s → %s\nТекст: %s%s", 
                kind, getFormattedCreationTime(), fromEmail, toEmail, body, signStatus);
    }
}