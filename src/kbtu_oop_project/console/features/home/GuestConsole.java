package kbtu_oop_project.console.features.home;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.Optional;
import java.util.Scanner;

public final class GuestConsole {

    private GuestConsole() {
        throw new UnsupportedOperationException("Это утилитарный класс для гостевого экрана.");
    }

    public static User guestHome(UniversityDatabase db, Scanner in) {
        while (true) {
            ConsoleUi.header("ДОБРО ПОЖАЛОВАТЬ В СИСТЕМУ ПЛАТОНУС КБТУ");
            System.out.println("1. Войти в систему (Авторизация)");
            System.out.println("2. Выйти из симулятора");
            System.out.print("\nВыберите действие (1-2): ");
            
            String choice = ConsoleUi.trim(in.nextLine());
            
            switch (choice) {
                case "1" -> {
                    User authenticatedUser = handleLogin(db, in);
                    if (authenticatedUser != null) {
                        return authenticatedUser;
                    }
                }
                case "2" -> {
                    return null;
                }
                default -> ConsoleUi.printlnErr("Неверный ввод! Пожалуйста, выберите пункт 1 или 2.");
            }
        }
    }

    private static User handleLogin(UniversityDatabase db, Scanner in) {
        ConsoleUi.header("ФОРМА АВТОРИЗАЦИИ");
        System.out.print("Введите ваш корпоративный Email (e.g., @kbtu.kz): ");
        String email = ConsoleUi.trim(in.nextLine()).toLowerCase();
        
        System.out.print("Введите пароль: ");
        String password = in.nextLine(); 

        Optional<User> userOpt = db.findAllUsers().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().equals(email))
                .findFirst();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (user.authenticate(password)) { 
                ConsoleUi.printlnOk("Авторизация успешна! Добро пожаловать, " + user.getFirstName() + ".");
                return user;
            }
        }

        ConsoleUi.printlnErr("Ошибка аутентификации! Неверный Email или пароль.");
        return null;
    }
}