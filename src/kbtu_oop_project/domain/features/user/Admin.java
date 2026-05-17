package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.misc.Log;

import java.util.List;
import java.util.Scanner;

public class Admin extends Employee {

    private static final long serialVersionUID = 1L;

    public Admin() {
        super();
    }

    public Admin(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password);
    }

    @Override
    public void login() {
        System.out.println("Администратор " + getEmail() + " вошел в панель управления.");
    }

    public void seeLogFiles(List<Log> systemLogs) {
        System.out.println("\n======= СИСТЕМНЫЕ ЖУРНАЛЫ (ЛОГИ) =======");
        if (systemLogs == null || systemLogs.isEmpty()) {
            System.out.println("История логов пуста.");
            return;
        }

        for (Log log : systemLogs) {
            System.out.println(log);
        }
        System.out.println("========================================");
    }

    public void manageUsers(List<User> allUsers, Scanner scanner) {
        System.out.println("\n--- Управление пользователями ---");
        System.out.println("1. Посмотреть всех пользователей");
        System.out.println("2. Удалить пользователя (Drop)");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1 -> {
                System.out.println("\nСписок зарегистрированных в системе:");
                allUsers.forEach(System.out::println);
            }
            case 2 -> {
                System.out.print("Введите ID пользователя для удаления: ");
                String idToDrop = scanner.nextLine();
                
                boolean removed = allUsers.removeIf(user -> user.getId().equals(idToDrop));
                if (removed) {
                    System.out.println("Пользователь с ID " + idToDrop + " успешно удален.");
                } else {
                    System.out.println("Пользователь с таким ID не найден.");
                }
            }
            default -> System.out.println("Возврат в главное меню.");
        }
    }
}