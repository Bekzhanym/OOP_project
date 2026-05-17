package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.notification.Notification;
import kbtu_oop_project.domain.features.notification.Observer;
import kbtu_oop_project.domain.value.Language;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class User implements Observer, Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Language currentLanguage;
    private final List<Notification> inbox = new ArrayList<>();

    public User() {
        this.currentLanguage = Language.ENG; 
    }

    public User(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.currentLanguage = Language.ENG;
    }

    @Override
    public void update(Notification notification) {
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
        return passwordAttempt != null && passwordAttempt.equals(password);
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        this.password = newPassword;
    }

    public abstract void login();
    public abstract void logout();

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | %s (%s)", getUserRole(), id, getFullName(), email);
    }

    public List<Notification> getInbox() { return inbox; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Language getCurrentLanguage() { return currentLanguage; }
    public void setCurrentLanguage(Language currentLanguage) { this.currentLanguage = currentLanguage; }
}