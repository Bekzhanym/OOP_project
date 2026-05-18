package kbtu_oop_project.console.features.home;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.value.Role;
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

        Optional<User> userOpt = db.findByEmailIgnoreCase(email);

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

    private static void handleRegistration(UniversityDatabase db, Scanner in) {
        ConsoleUi.header("РЕГИСТРАЦИЯ В СИСТЕМЕ");
        System.out.println("По умолчанию создаётся аккаунт студента (1 курс).");
        System.out.println("Администратор может позже назначить другую роль.");
        System.out.println("(0 — отмена)");
        System.out.print("Продолжить? (Enter / 0): ");
        if ("0".equals(ConsoleUi.trim(in.nextLine()))) {
            return;
        }

        String firstName = ConsoleUi.promptRequired(in, "Имя");
        String lastName = ConsoleUi.promptRequired(in, "Фамилия");

        String email;
        while (true) {
            email = ConsoleUi.promptRequired(in, "Email (@kbtu.kz)").toLowerCase();
            if (!email.endsWith("@kbtu.kz") || email.length() <= 8) {
                ConsoleUi.printlnErr("Допустим только корпоративный email домена @kbtu.kz.");
                continue;
            }
            if (db.isEmailTaken(email)) {
                ConsoleUi.printlnErr("Пользователь с таким email уже зарегистрирован.");
                continue;
            }
            break;
        }

        String password;
        while (true) {
            System.out.print("Пароль (минимум 6 символов): ");
            password = in.nextLine();
            if (password == null || password.length() < 6) {
                ConsoleUi.printlnErr("Пароль слишком короткий.");
                continue;
            }
            System.out.print("Повторите пароль: ");
            String confirm = in.nextLine();
            if (!password.equals(confirm)) {
                ConsoleUi.printlnErr("Пароли не совпадают.");
                continue;
            }
            break;
        }

        String id = db.allocateUserId(Role.STUDENT);
        Student student = new Student(id, firstName, lastName, email, password, 1);
        db.addUser(student);
        db.recordAudit("REGISTER_STUDENT " + email);

        ConsoleUi.printlnOk("Регистрация завершена (ID: " + id + ", студент 1 курс). Можно войти в систему.");
    }
}
