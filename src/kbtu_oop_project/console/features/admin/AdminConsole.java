package kbtu_oop_project.console.features.admin;

import kbtu_oop_project.UniversityApp;
import kbtu_oop_project.application.usecase.AdvancedSearchUseCase;
import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.user.UserRoleFormatter;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public final class AdminConsole {

    private AdminConsole() {
    }

    public static boolean adminMenu(User adminSelf, UniversityDatabase db, Scanner in) {
        AdvancedSearchUseCase search = UniversityApp.search();
        System.out.println("  1 — Список пользователей");
        System.out.println("  2 — Список курсов");
        System.out.println("  3 — Поиск по regex (email пользователя или название курса)");
        System.out.println("  4 — Сохранить в data/university-state.ser");
        System.out.println("  5 — Удалить пользователя по email");
        System.out.println("  6 — Удалить курс по коду");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                ConsoleUi.header("Пользователи");
                for (User u : db.getUsers()) {
                    System.out.println(" • " + u.getId() + " | " + UserRoleFormatter.describe(u)
                            + " | " + u.getEmail());
                }
                break;
            case "2":
                ConsoleUi.header("Курсы");
                for (Course c : db.getCourses()) {
                    System.out.println(" • " + c.getCourseCode() + " — " + c.getCourseName()
                            + " (" + c.getCredits() + " cr)");
                }
                break;
            case "3":
                System.out.print("Regex (Java Pattern): ");
                String rx = ConsoleUi.trim(in.nextLine());
                try {
                    List<Object> hits = search.execute(rx);
                    ConsoleUi.header("Результаты поиска (" + hits.size() + ")");
                    for (Object o : hits) {
                        System.out.println(" • " + o);
                    }
                } catch (Exception ex) {
                    ConsoleUi.printlnErr("Ошибка regex: " + ex.getMessage());
                }
                break;
            case "4":
                db.saveData();
                ConsoleUi.printlnOk("Сохранено.");
                break;
            case "5":
                System.out.print("Email пользователя для удаления: ");
                String delEmail = ConsoleUi.trim(in.nextLine());
                Optional<User> victim = db.findByEmailIgnoreCase(delEmail);
                if (victim.isEmpty()) {
                    ConsoleUi.printlnErr("Пользователь с таким email не найден.");
                    break;
                }
                User toRemove = victim.get();
                if (sameEmail(adminSelf, toRemove)) {
                    ConsoleUi.printlnErr("Нельзя удалить свой аккаунт.");
                    break;
                }
                if (db.removeUser(toRemove)) {
                    ConsoleUi.printlnOk("Пользователь удалён.");
                } else {
                    ConsoleUi.printlnErr("Не удалось удалить.");
                }
                break;
            case "6":
                System.out.print("Код курса для удаления: ");
                String courseCode = ConsoleUi.trim(in.nextLine());
                if (db.removeCourseByCode(courseCode)) {
                    ConsoleUi.printlnOk("Курс удалён.");
                } else {
                    ConsoleUi.printlnErr("Курс не найден.");
                }
                break;
            case "0":
                return true;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
        return false;
    }

    private static boolean sameEmail(User a, User b) {
        if (a.getEmail() == null || b.getEmail() == null) {
            return false;
        }
        return a.getEmail().trim().equalsIgnoreCase(b.getEmail().trim());
    }
}
