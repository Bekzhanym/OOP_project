package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.notification.Notification;
import kbtu_oop_project.domain.features.notification.Observer;
import kbtu_oop_project.domain.value.Language;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class User implements Observer, Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Language currentLanguage;

    private final List<Notification> inbox = new ArrayList<>();

    protected User() {
        this.id = "GENERIC_ID";
        this.currentLanguage = Language.ENG;
    }

    protected User(String id, String firstName, String lastName, String email, String password) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID пользователя не может быть пустым.");
        }
        this.id = id.trim();

        this.firstName = Objects.requireNonNullElse(firstName, "Name").trim();
        this.lastName = Objects.requireNonNullElse(lastName, "Surname").trim();

        this.email = validateAndNormalizeEmail(email);
        this.password = Objects.requireNonNull(password, "Пароль не может быть null.");
        this.currentLanguage = Language.ENG;
    }

    @Override
    public final void update(Notification notification) {
        if (notification != null) {
            inbox.add(notification);
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUserRole() {
        return this.getClass().getSimpleName();
    }

    public boolean authenticate(String passwordAttempt) {
        return passwordAttempt != null && passwordAttempt.equals(this.password);
    }

    String internalPassword() {
        return password;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (!authenticate(oldPassword)) {
            throw new SecurityException("Аутентификация провалена. Неверный текущий пароль.");
        }
        setNewPasswordInternal(newPassword);
    }

    public final void forceResetPassword(String newPassword) {
        setNewPasswordInternal(newPassword);
    }

    private void setNewPasswordInternal(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Новый пароль не может быть пустым.");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Пароль слишком короткий. Минум 6 символов.");
        }
        this.password = newPassword;
        System.out.println("🔒 Пароль для пользователя " + email + " успешно обновлен.");
    }

    public abstract void login();

    public abstract void logout();

    private String validateAndNormalizeEmail(String inputEmail) {
        if (inputEmail == null || inputEmail.isBlank()) {
            throw new IllegalArgumentException("Email адрес обязателен для регистрации в системе.");
        }
        String normalized = inputEmail.trim().toLowerCase();
        if (!normalized.contains("@")) {
            throw new IllegalArgumentException("Некорректный формат email адреса: " + inputEmail);
        }
        return normalized;
    }

    public List<Notification> getInbox() { 
        return Collections.unmodifiableList(inbox); 
    }

    public String getId() { return id; }

    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        this.firstName = Objects.requireNonNullElse(firstName, "").trim(); 
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        this.lastName = Objects.requireNonNullElse(lastName, "").trim(); 
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = validateAndNormalizeEmail(email); 
    }

    public Language getCurrentLanguage() { return currentLanguage; }
    public void setCurrentLanguage(Language currentLanguage) { 
        this.currentLanguage = currentLanguage != null ? currentLanguage : Language.ENG; 
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | %s (%s)", getUserRole(), id, getFullName(), email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof User)) return false;
        User other = (User) o;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}