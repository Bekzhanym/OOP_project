package edu.university.domain.model;

import edu.university.domain.value.Language;

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

    public abstract void login();

    public abstract void logout();

    public abstract void changePassword(String newPassword);

    /** Minimal authentication hook for console/API flows (compare stored password). */
    public boolean authenticate(String passwordAttempt) {
        return passwordAttempt != null && passwordAttempt.equals(password);
    }

    @Override
    public void update(Notification notification) {
        inbox.add(notification);
    }

    public List<Notification> getInbox() {
        return inbox;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(Language currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
}
