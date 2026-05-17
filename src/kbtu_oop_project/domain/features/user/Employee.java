package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.misc.EmployeeMessage;
import kbtu_oop_project.domain.value.MessageKind;

import java.util.ArrayList;
import java.util.List;

public class Employee extends User {

    private static final long serialVersionUID = 1L;

    private final List<EmployeeMessage> messageBox = new ArrayList<>();

    public Employee() {
        super();
    }

    public Employee(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password);
    }

    @Override
    public void login() {
        System.out.println("Сотрудник " + getEmail() + " вошел в систему.");
    }

    @Override
    public void logout() {
        System.out.println("Сотрудник " + getEmail() + " вышел из системы.");
    }

    public void sendMessage(Employee recipient, String body, MessageKind kind, boolean requiresDeanSignature) {
        if (recipient == null) {
            System.out.println("Ошибка: Получатель не найден.");
            return;
        }

        EmployeeMessage message = new EmployeeMessage(
                this.getId(), 
                this.getEmail(), 
                recipient.getEmail(), 
                kind, 
                body, 
                requiresDeanSignature
        );

        recipient.receiveMessage(message);
        this.messageBox.add(message);
    }

    public void receiveMessage(EmployeeMessage message) {
        if (message != null) {
            this.messageBox.add(message);
        }
    }

    public List<EmployeeMessage> getMessageBox() {
        return messageBox;
    }

    public String getEmployeeId() {
        return getId();
    }

    public void setEmployeeId(String employeeId) {
        setId(employeeId);
    }
}