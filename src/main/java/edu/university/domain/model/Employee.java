package edu.university.domain.model;

public class Employee extends User {

    private static final long serialVersionUID = 1L;

    private String employeeId;

    @Override
    public void login() {
    }

    @Override
    public void logout() {
    }

    @Override
    public void changePassword(String newPassword) {
        setPassword(newPassword);
    }

    public void sendMessage(Employee recipient, String message) {
    }

    public void receiveMessage(String message) {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
