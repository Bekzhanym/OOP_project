package kbtu_oop_project.console.features.admin;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.user.UserRoleFormatter;
import kbtu_oop_project.domain.exception.SupervisorQualificationException;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.research.Researcher;
import kbtu_oop_project.domain.features.user.Student4thYear;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public final class AdminConsole {

    private AdminConsole() {
    }

    public static boolean adminMenu(User adminSelf, UniversityDatabase db, Scanner in) {
        System.out.println("  1 — Список пользователей");
        System.out.println("  2 — Список курсов");
        System.out.println("  3 — Поиск по regex (email пользователя или название курса)");
        System.out.println("  4 — Сохранить в data/university-state.ser");
        System.out.println("  5 — Удалить пользователя по email");
        System.out.println("  6 — Удалить курс по коду");
        System.out.println("  7 — Журнал аудита и структурированные Log");
        System.out.println("  8 — Сменить пароль пользователя по email");
        System.out.println("  9 — Назначить научного руководителя студенту 4 курса");
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
                    String lessonInfo = "—";
                    if (c.getLesson() != null && c.getLesson().getType() != null) {
                        lessonInfo = c.getLesson().getType().name();
                    }
                    System.out.println(" • " + c.getCourseCode() + " — " + c.getCourseName()
                            + " (" + c.getCredits() + " cr)"
                            + " | type=" + c.getCourseType()
                            + " | major=" + (c.getIntendedMajor() != null ? c.getIntendedMajor() : "—")
                            + ", year=" + (c.getIntendedYearOfStudy() > 0 ? c.getIntendedYearOfStudy() : "—")
                            + " | lesson=" + lessonInfo);
                }
                break;
            case "3":
                System.out.print("Regex (Java Pattern): ");
                String rx = ConsoleUi.trim(in.nextLine());
                try {
                    List<Object> hits = db.advancedSearch(rx);
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
            case "7":
                ConsoleUi.header("Строковый журнал (audit)");
                List<String> lines = db.getLogLines();
                int from = Math.max(0, lines.size() - 40);
                for (int i = from; i < lines.size(); i++) {
                    System.out.println(lines.get(i));
                }
                ConsoleUi.header("Структурированные записи Log");
                for (Log lg : db.getLogs()) {
                    System.out.println(lg.getTimestamp() + " | " + lg.getAction()
                            + " | userId=" + lg.getUserId());
                }
                break;
            case "8":
                System.out.print("Email пользователя: ");
                String pwdEmail = ConsoleUi.trim(in.nextLine());
                Optional<User> pwdUser = db.findByEmailIgnoreCase(pwdEmail);
                if (pwdUser.isEmpty()) {
                    ConsoleUi.printlnErr("Пользователь не найден.");
                    break;
                }
                String np = ConsoleUi.promptRequired(in, "Новый пароль");
                pwdUser.get().changePassword(np);
                db.recordAudit("ADMIN_PASSWORD_RESET " + pwdEmail.trim());
                Log lg = new Log();
                lg.setAction("admin_password_reset");
                lg.setUserId(pwdUser.get().getId());
                lg.setTimestamp(LocalDate.now());
                db.recordStructured(lg);
                ConsoleUi.printlnOk("Пароль обновлён.");
                break;
            case "9":
                assignFourthYearSupervisorFlow(db, in);
                break;
            case "0":
                return true;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
        return false;
    }

    private static void assignFourthYearSupervisorFlow(UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Научный руководитель (4 курс)");
        System.out.print("Email студента (Student4thYear): ");
        String stEmail = ConsoleUi.trim(in.nextLine());
        Optional<User> su = db.findByEmailIgnoreCase(stEmail);
        if (su.isEmpty() || !(su.get() instanceof Student4thYear sy)) {
            ConsoleUi.printlnErr("Нужен аккаунт роли «студент 4 курса».");
            return;
        }
        System.out.print("Email руководителя (Teacher / Professor / Research staff — как Researcher): ");
        String svEmail = ConsoleUi.trim(in.nextLine());
        Optional<User> sv = db.findByEmailIgnoreCase(svEmail);
        if (sv.isEmpty() || !(sv.get() instanceof Researcher researcher)) {
            ConsoleUi.printlnErr("Руководитель должен быть исследователем (Researcher).");
            return;
        }
        try {
            sy.setSupervisor(researcher);
            ConsoleUi.printlnOk("Руководитель назначен: h-index=" + researcher.getHIndex());
            db.recordAudit("SUPERVISOR_SET " + stEmail + " → " + svEmail);
        } catch (SupervisorQualificationException ex) {
            ConsoleUi.printlnErr(ex.getMessage());
        }
    }

    private static boolean sameEmail(User a, User b) {
        if (a.getEmail() == null || b.getEmail() == null) {
            return false;
        }
        return a.getEmail().trim().equalsIgnoreCase(b.getEmail().trim());
    }
}
