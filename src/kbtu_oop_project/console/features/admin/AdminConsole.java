package kbtu_oop_project.console.features.admin;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.user.UserRoleFormatter;
import kbtu_oop_project.domain.exception.SupervisorQualificationException;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.time.LocalDate;
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
            case "1":
                ConsoleUi.header("Список пользователей университета");
                for (User u : db.getUsers()) {
                    System.out.println(" • ID: " + u.getId() + " | Роль: " + UserRoleFormatter.describe(u)
                            + " | Email: " + u.getEmail());
                }
                break;
                
            case "2":
                ConsoleUi.header("Доступные курсы");
                for (Course c : db.getCourses()) {
                    String lessonInfo = (c.getLesson() != null && c.getLesson().getType() != null) 
                            ? c.getLesson().getType().name() : "—";
                            
                    System.out.println(" • " + c.getCourseCode() + " — " + c.getCourseName()
                            + " (" + c.getCredits() + " cr)"
                            + " | Тип: " + c.getCourseType()
                            + " | Специальность: " + (c.getIntendedMajor() != null ? c.getIntendedMajor() : "Все")
                            + ", Курс: " + (c.getIntendedYearOfStudy() > 0 ? c.getIntendedYearOfStudy() : "Все")
                            + " | Занятие: " + lessonInfo);
                }
                break;
                
            case "3":
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
                break;
                
            case "4":
                db.saveData();
                ConsoleUi.printlnOk("Состояние системы успешно сериализовано в файлы данных.");
                break;
                
            case "5":
                String delEmail = ConsoleUi.promptRequired(in, "Email пользователя для удаления");
                Optional<User> victim = db.findByEmailIgnoreCase(delEmail);
                if (victim.isEmpty()) {
                    ConsoleUi.printlnErr("Пользователь с таким email не найден.");
                    break;
                }
                User toRemove = victim.get();
                if (sameEmail(adminSelf, toRemove)) {
                    ConsoleUi.printlnErr("Критическая ошибка: невозможно удалить собственный активный аккаунт.");
                    break;
                }
                if (db.removeUser(toRemove)) {
                    ConsoleUi.printlnOk("Пользователь успешно удалён из базы данных.");
                } else {
                    ConsoleUi.printlnErr("Не удалось выполнить удаление.");
                }
                break;
                
            case "6":
                String courseCode = ConsoleUi.promptRequired(in, "Код курса для удаления (например, CS101)");
                if (db.removeCourseByCode(courseCode)) {
                    ConsoleUi.printlnOk("Курс успешно удалён.");
                } else {
                    ConsoleUi.printlnErr("Курс с кодом '" + courseCode + "' не найден.");
                }
                break;
                
            case "7":
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
                break;
                
            case "8":
                String pwdEmail = ConsoleUi.promptRequired(in, "Email целевого пользователя");
                Optional<User> pwdUser = db.findByEmailIgnoreCase(pwdEmail);
                if (pwdUser.isEmpty()) {
                    ConsoleUi.printlnErr("Пользователь не найден.");
                    break;
                }
                String np = ConsoleUi.promptRequired(in, "Введите новый пароль");
                pwdUser.get().changePassword(np);
                
                db.recordAudit("ADMIN_PASSWORD_RESET " + pwdEmail.trim());
                Log lg = new Log();
                lg.setAction("admin_password_reset");
                lg.setUserId(pwdUser.get().getId());
                lg.setTimestamp(LocalDate.now());
                db.recordStructured(lg);
                
                ConsoleUi.printlnOk("Пароль успешно принудительно изменён.");
                break;
                
            case "9":
                assignFourthYearSupervisorFlow(db, in);
                break;
                
            default:
                ConsoleUi.printlnErr("Неизвестная команда. Повторите ввод.");
        }
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
        
        if (sv.isEmpty() || !sv.get().isResearcher()) {
            ConsoleUi.printlnErr("Ошибка: Выбранный руководитель не зарегистрирован как Исследователь (Researcher).");
            return;
        }
        
        try {
            student.setSupervisor(sv.get()); 
            ConsoleUi.printlnOk("Руководитель успешно назначен! Подтвержденный h-index: " 
                    + sv.get().getResearcherProfile().getHIndex());
            db.recordAudit("SUPERVISOR_SET " + stEmail + " → " + svEmail);
        } catch (SupervisorQualificationException ex) {
            ConsoleUi.printlnErr("Отклонено системой: " + ex.getMessage());
        }
    }

    private static boolean sameEmail(User a, User b) {
        if (a.getEmail() == null || b.getEmail() == null) {
            return false;
        }
        return a.getEmail().trim().equalsIgnoreCase(b.getEmail().trim());
    }
}