package kbtu_oop_project.console.features.admin;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.user.UserRoleFormatter;
import kbtu_oop_project.domain.exception.SupervisorQualificationException;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.user.Admin;
import kbtu_oop_project.domain.features.user.PendingUser;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Student4thYear;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.value.ManagerType;
import kbtu_oop_project.domain.value.Role;
import kbtu_oop_project.domain.value.TeacherTitle;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public final class AdminConsole {

    private AdminConsole() {
        throw new UnsupportedOperationException("Это утилитарный класс для CLI.");
    }

    public static void start(User adminSelf, UniversityDatabase db, Scanner in) {
        while (true) {
            ConsoleUi.header("Панель Администратора");
            System.out.println("  1 — Список пользователей");
            System.out.println("  2 — Список курсов");
            System.out.println("  3 — Поиск по regex (email пользователя или название курса)");
            System.out.println("  4 — Сохранить состояние системы (Сериализация)");
            System.out.println("  5 — Удалить пользователя по email");
            System.out.println("  6 — Удалить курс по коду");
            System.out.println("  7 — Журнал аудита и структурированные Logs");
            System.out.println("  8 — Сменить пароль пользователя");
            System.out.println("  9 — Назначить научного руководителя студенту 4 курса");
            System.out.println(" 10 — Изменить роль существующего пользователя");
            System.out.println(" 11 — Зарегистрировать (создать) нового пользователя ➕"); // ДОБАВЛЕНО
            System.out.println("  0 — Выйти из аккаунта");
            
            System.out.print("Выбор: ");
            String choice = ConsoleUi.trim(in.nextLine());
            
            if ("0".equals(choice)) {
                ConsoleUi.printlnOk("Выход из панели администратора выполнен.");
                break;
            }
            
            handleMenuChoice(choice, adminSelf, db, in);
        }
    }

    private static void handleMenuChoice(String choice, User adminSelf, UniversityDatabase db, Scanner in) {
        switch (choice) {
            case "1" -> showUsersFlow(db);
            case "2" -> showCoursesFlow(db);
            case "3" -> regexSearchFlow(db, in);
            case "4" -> saveDataFlow(db);
            case "5" -> removeUserFlow(adminSelf, db, in);
            case "6" -> removeCourseFlow(db, in);
            case "7" -> showAuditLogsFlow(db);
            case "8" -> resetPasswordFlow(db, in);
            case "9" -> assignFourthYearSupervisorFlow(db, in);
            case "10" -> assignUserRoleFlow(db, in);
            case "11" -> createNewUserFlow(db, in); // ДОБАВЛЕНО
            default -> ConsoleUi.printlnErr("Неизвестная команда. Повторите ввод.");
        }
    }

    private static void showUsersFlow(UniversityDatabase db) {
        ConsoleUi.header("Список пользователей университета");
        for (User u : db.findAllUsers()) {
            String pending = u instanceof PendingUser ? " [студент по умолчанию]" : "";
            System.out.println(" • ID: " + u.getId() + " | Роль: " + UserRoleFormatter.describe(u)
                    + pending + " | Email: " + u.getEmail());
        }
    }

    private static void showCoursesFlow(UniversityDatabase db) {
        ConsoleUi.header("Доступные курсы");
        for (Course c : db.findAllCourses()) {
            var lessons = c.getLessons();
            String lessonInfo = (!lessons.isEmpty() && lessons.get(0).getType() != null)
                    ? lessons.get(0).getType().name() : "—";

            System.out.println(" • " + c.getCourseCode() + " — " + c.getCourseName()
                    + " (" + c.getCredits() + " cr)"
                    + " | Тип: " + c.getCourseType()
                    + " | Специальность: " + (c.getIntendedMajor() != null ? c.getIntendedMajor() : "Все")
                    + ", Курс: " + (c.getIntendedYearOfStudy() > 0 ? c.getIntendedYearOfStudy() : "Все")
                    + " | Занятие: " + lessonInfo);
        }
    }

    private static void regexSearchFlow(UniversityDatabase db, Scanner in) {
        String rx = ConsoleUi.promptRequired(in, "Введите регулярное выражение (Java Regex)");
        try {
            List<Object> hits = db.advancedSearch(rx);
            ConsoleUi.header("Результаты поиска по шаблону (" + hits.size() + ")");
            for (Object o : hits) {
                System.out.println(" • " + o);
            }
        } catch (Exception ex) {
            ConsoleUi.printlnErr("Ошибка в синтаксисе регулярного выражения: " + ex.getMessage());
        }
    }

    private static void saveDataFlow(UniversityDatabase db) {
        db.saveData();
        ConsoleUi.printlnOk("Состояние системы успешно сериализовано в файлы данных.");
    }

    private static void removeUserFlow(User adminSelf, UniversityDatabase db, Scanner in) {
        String delEmail = ConsoleUi.promptRequired(in, "Email пользователя для удаления");
        Optional<User> victim = db.findByEmailIgnoreCase(delEmail);
        if (victim.isEmpty()) {
            ConsoleUi.printlnErr("Пользователь с таким email не найден.");
            return;
        }
        User toRemove = victim.get();
        if (sameEmail(adminSelf, toRemove)) {
            ConsoleUi.printlnErr("Критическая ошибка: невозможно удалить собственный активный аккаунт.");
            return;
        }
        if (db.removeUser(toRemove)) {
            ConsoleUi.printlnOk("Пользователь успешно удалён из базы данных.");
        } else {
            ConsoleUi.printlnErr("Не удалось выполнить удаление.");
        }
    }

    private static void removeCourseFlow(UniversityDatabase db, Scanner in) {
        String courseCode = ConsoleUi.promptRequired(in, "Код курса для удаления (например, CS101)");
        if (db.removeCourseByCode(courseCode)) {
            ConsoleUi.printlnOk("Курс успешно удалён.");
        } else {
            ConsoleUi.printlnErr("Курс с кодом '" + courseCode + "' не найден.");
        }
    }

    private static void showAuditLogsFlow(UniversityDatabase db) {
        ConsoleUi.header("Текстовый журнал аудита (Последние 40 записей)");
        List<String> lines = db.getLogLines();
        int from = Math.max(0, lines.size() - 40);
        for (int i = from; i < lines.size(); i++) {
            System.out.println(lines.get(i));
        }
        
        ConsoleUi.header("Структурированные объекты логов (Log Objects)");
        for (Log lg : db.getLogs()) {
            System.out.println("[" + lg.getTimestamp() + "] Действие: " + lg.getAction()
                    + " | Инициатор ID: " + lg.getUserId());
        }
    }

    private static void resetPasswordFlow(UniversityDatabase db, Scanner in) {
        String pwdEmail = ConsoleUi.promptRequired(in, "Email целевого пользователя");
        Optional<User> pwdUser = db.findByEmailIgnoreCase(pwdEmail);
        if (pwdUser.isEmpty()) {
            ConsoleUi.printlnErr("Пользователь не найден.");
            return;
        }
        String np = ConsoleUi.promptRequired(in, "Введите новый пароль");
        
        User user = pwdUser.get();
        user.forceResetPassword(np);

        db.recordStructured(new Log(user.getId(), "admin_password_reset"));
        db.recordAudit("ADMIN_PASSWORD_RESET " + pwdEmail.trim());
        
        ConsoleUi.printlnOk("Пароль успешно принудительно изменён.");
    }

    private static void assignFourthYearSupervisorFlow(UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Назначение научного руководителя дипломникам");
        String stEmail = ConsoleUi.promptRequired(in, "Email студента");
        Optional<User> su = db.findByEmailIgnoreCase(stEmail);
        
        if (su.isEmpty() || !(su.get() instanceof Student student) || student.getYearOfStudy() != 4) {
            ConsoleUi.printlnErr("Ошибка: Данный пользователь не является студентом 4 курса.");
            return;
        }
        
        String svEmail = ConsoleUi.promptRequired(in, "Email научного руководителя");
        Optional<User> sv = db.findByEmailIgnoreCase(svEmail);
        
        if (sv.isEmpty() || !(sv.get() instanceof Teacher)) {
            ConsoleUi.printlnErr("Ошибка: Выбранный руководитель не является преподавателем с профилем исследователя.");
            return;
        }

        Teacher supervisor = (Teacher) sv.get();

        if (!(student instanceof Student4thYear fourthYear)) {
            ConsoleUi.printlnErr("Ошибка: Студент не зарегистрирован как студент 4 курса.");
            return;
        }

        try {
            fourthYear.setSupervisor(supervisor);

            ConsoleUi.printlnOk("Руководитель успешно назначен! Подтвержденный h-index: " + supervisor.getHIndex());

            db.recordStructured(new Log(student.getId(), "supervisor_assigned"));
            db.recordAudit("SUPERVISOR_SET " + stEmail + " → " + svEmail);
            
        } catch (SupervisorQualificationException ex) {
            ConsoleUi.printlnErr("Отклонено академической системой КБТУ: " + ex.getMessage());
        }
    }

    // ДОБАВЛЕНО: ФЛОУ ПРЯМОЙ РЕГИСТРАЦИИ ПОЛЬЗОВАТЕЛЕЙ АДМИНИСТРАТОРОМ
    private static void createNewUserFlow(UniversityDatabase db, Scanner in) {
        ConsoleUi.header("РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ");
        
        String email = "";
        while (true) {
            System.out.print("Введите корпоративный Email (@kbtu.kz): ");
            email = ConsoleUi.trim(in.nextLine()).toLowerCase();
            if (email.endsWith("@kbtu.kz") && email.length() > 8) {
                if (db.findByEmailIgnoreCase(email).isPresent()) {
                    ConsoleUi.printlnErr("[Ошибка] Пользователь с таким Email уже существует!");
                    return;
                }
                break;
            } else {
                ConsoleUi.printlnErr("[Ошибка] Email должен принадлежать домену @kbtu.kz!");
            }
        }

        System.out.print("Введите ID (например, S-02, T-01, M-03): ");
        String id = ConsoleUi.trim(in.nextLine()).toUpperCase();
        System.out.print("Введите Имя: ");
        String firstName = ConsoleUi.trim(in.nextLine());
        System.out.print("Введите Фамилию: ");
        String lastName = ConsoleUi.trim(in.nextLine());
        System.out.print("Придумайте пароль: ");
        String password = ConsoleUi.trim(in.nextLine());

        System.out.println("\nВыберите базовую академическую роль:");
        System.out.println("1 — Студент (1-3 курс)");
        System.out.println("2 — Студент 4 курса");
        System.out.println("3 — Преподаватель / Профессор");
        System.out.println("4 — Менеджер");
        System.out.println("5 — Администратор");
        System.out.print("Выбор: ");
        String roleChoice = ConsoleUi.trim(in.nextLine());

        Role role = Role.STUDENT;
        int studentYear = 1;
        ManagerType managerType = ManagerType.DEPARTMENT;
        TeacherTitle teacherTitle = TeacherTitle.LECTOR;

        switch (roleChoice) {
            case "1" -> {
                role = Role.STUDENT;
                studentYear = ConsoleUi.promptInt(in, "Укажите курс (1-3)", 1, 3);
            }
            case "2" -> role = Role.STUDENT_4TH_YEAR;
            case "3" -> {
                role = Role.TEACHER;
                teacherTitle = promptTeacherTitle(in);
            }
            case "4" -> {
                role = Role.MANAGER;
                managerType = promptManagerType(in);
            }
            case "5" -> role = Role.ADMIN;
            default -> {
                System.out.println("По умолчанию выбрана роль: Студент (1 курс)");
            }
        }

        try {
            // Создаем сущность через фабрику проекта
            User newUser = kbtu_oop_project.application.factory.UserFactory.createUser(
                    role, id, firstName + " " + lastName, email, password
            );

            // Сохраняем в единую UniversityDatabase
            db.addUser(newUser);

            // Твоя внутренняя логика инициализации специфичных полей (курса, титулов, типа менеджера)
            db.assignUserRole(email, role, studentYear, managerType, teacherTitle);

            db.recordStructured(new Log(id, "admin_create_user:" + role.name()));
            db.recordAudit("ADMIN_CREATE_USER " + email + " as " + role.name());

            ConsoleUi.printlnOk("\n[УСПЕХ] Пользователь успешно создан и настроен в базе Платонуса!");
        } catch (Exception ex) {
            ConsoleUi.printlnErr("Ошибка создания пользователя фабрикой: " + ex.getMessage());
        }
    }

    private static void assignUserRoleFlow(UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Изменение роли пользователя");
        System.out.println("Новые пользователи регистрируются как студенты 1 курса.");
        System.out.println("Здесь можно назначить другую роль по email.");

        String email = ConsoleUi.promptRequired(in, "Email пользователя");
        Optional<User> target = db.findByEmailIgnoreCase(email);
        if (target.isEmpty()) {
            ConsoleUi.printlnErr("Пользователь не найден.");
            return;
        }
        if (target.get() instanceof Admin) {
            ConsoleUi.printlnErr("Роль администратора нельзя изменить через это меню.");
            return;
        }

        System.out.println("1 — Студент (1–3 курс)");
        System.out.println("2 — Студент 4 курса");
        System.out.println("3 — Преподаватель");
        System.out.println("4 — Профессор");
        System.out.println("5 — Научный сотрудник");
        System.out.println("6 — Менеджер");
        System.out.println("7 — Сотрудник");
        System.out.println("8 — Администратор");
        System.out.print("Роль: ");
        String roleChoice = ConsoleUi.trim(in.nextLine());

        Role role;
        int studentYear = 1;
        ManagerType managerType = ManagerType.DEPARTMENT;
        TeacherTitle teacherTitle = TeacherTitle.LECTOR;

        switch (roleChoice) {
            case "1" -> {
                role = Role.STUDENT;
                studentYear = ConsoleUi.promptInt(in, "Курс", 1, 3);
            }
            case "2" -> role = Role.STUDENT_4TH_YEAR;
            case "3" -> {
                role = Role.TEACHER;
                teacherTitle = promptTeacherTitle(in);
            }
            case "4" -> role = Role.PROFESSOR;
            case "5" -> role = Role.RESEARCH_STAFF;
            case "6" -> {
                role = Role.MANAGER;
                managerType = promptManagerType(in);
            }
            case "7" -> role = Role.EMPLOYEE;
            case "8" -> role = Role.ADMIN;
            default -> {
                ConsoleUi.printlnErr("Неизвестная роль.");
                return;
            }
        }

        if (db.assignUserRole(email, role, studentYear, managerType, teacherTitle)) {
            ConsoleUi.printlnOk("Роль назначена: " + UserRoleFormatter.describe(
                    db.findByEmailIgnoreCase(email).orElse(target.get())));
            db.recordStructured(new Log(target.get().getId(), "role_assigned:" + role.name()));
        } else {
            ConsoleUi.printlnErr("Не удалось назначить роль.");
        }
    }

    private static TeacherTitle promptTeacherTitle(Scanner in) {
        System.out.println("1 — Tutor  2 — Lector  3 — Senior Lector  4 — Professor");
        return switch (ConsoleUi.trim(in.nextLine())) {
            case "1" -> TeacherTitle.TUTOR;
            case "3" -> TeacherTitle.SENIOR_LECTOR;
            case "4" -> TeacherTitle.PROFESSOR;
            default -> TeacherTitle.LECTOR;
        };
    }

    private static ManagerType promptManagerType(Scanner in) {
        System.out.println("1 — Office of Registrar  2 — Department Manager");
        return "1".equals(ConsoleUi.trim(in.nextLine()))
                ? ManagerType.OFFICE_REGISTRATOR
                : ManagerType.DEPARTMENT;
    }

    private static boolean sameEmail(User a, User b) {
        if (a.getEmail() == null || b.getEmail() == null) {
            return false;
        }
        return a.getEmail().trim().equalsIgnoreCase(b.getEmail().trim());
    }
}