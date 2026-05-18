package kbtu_oop_project.console.features.session;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.admin.AdminConsole;
import kbtu_oop_project.console.features.employee.GenericEmployeeConsole;
import kbtu_oop_project.console.features.student.StudentConsole;
import kbtu_oop_project.console.features.teacher.TeacherConsole;
import kbtu_oop_project.console.features.user.UserRoleFormatter;
import kbtu_oop_project.console.features.manager.ManagerConsole;
import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.user.Admin;
import kbtu_oop_project.domain.features.user.Employee;
import kbtu_oop_project.domain.features.user.Manager;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.Scanner;

public final class SessionDispatcher {

    private SessionDispatcher() {
        throw new UnsupportedOperationException("Это утилитарный класс-диспетчер.");
    }

    private static User extractCoreUser(User u) {
        if (u == null) return null;
        if (u.getClass().getSimpleName().contains("Researcher")) {
            try {
                var method = u.getClass().getMethod("getOriginalUser");
                return (User) method.invoke(u);
            } catch (Exception e) {
                return u;
            }
        }
        return u;
    }

    public static void runSessionForUser(User user, UniversityDatabase db, Scanner in) {
        boolean logout = false;
        User coreUser = extractCoreUser(user);

        while (!logout) {
            ConsoleUi.header("Личный кабинет — " + UserRoleFormatter.describe(user));
            System.out.println("  Пользователь: " + coreUser.getFirstName() + " " + coreUser.getLastName());
            System.out.println("  Email: <" + safeEmail(coreUser) + ">");
            System.out.println("────────────────────────────────────────────────");

            logout = dispatchSession(user, db, in);
            
            if (logout) {
                db.recordAudit("LOGOUT " + safeEmail(coreUser));
                db.recordStructured(new Log(coreUser.getId(), "logout"));
            }
        }
        ConsoleUi.printlnOk("Вы успешно вышли из системы.");
    }

    private static String safeEmail(User user) {
        return user.getEmail() != null ? user.getEmail() : "(no-email)";
    }

    private static boolean dispatchSession(User user, UniversityDatabase db, Scanner in) {
        User coreUser = extractCoreUser(user);

        if (coreUser instanceof Student student) {
            return StudentConsole.studentMenu(student, db, in);
        }

        
        if (coreUser instanceof Admin adminUser) {
            AdminConsole.start(adminUser, db, in);
            return true; 
        }
        
        
        if (coreUser instanceof Employee employee) {
            System.out.println("Куда вы хотите войти?");
            System.out.println("  1 — В рабочую панель (Кафедра / Менеджмент)");
            System.out.println("  2 — В корпоративную почту (Сообщения, Жалобы, Запросы)");
            System.out.println("  0 — Выйти из аккаунта");
            System.out.print("Выбор: ");
            String choice = ConsoleUi.trim(in.nextLine());
            
            if ("0".equals(choice)) return true; 
            
            if ("2".equals(choice)) {
                GenericEmployeeConsole.start(employee, db, in);
                return false; 
            }
            
            if ("1".equals(choice)) {
                
                if (employee instanceof Teacher teacher) {
                    return TeacherConsole.teacherMenu(teacher, db, in);
                }
                if (employee instanceof Manager manager) {
                    return ManagerConsole.managerMenu(manager, db, in);
                }
                
                
                ConsoleUi.printlnErr("У вашей группы сотрудников нет выделенной рабочей панели. Пользуйтесь корпоративной почтой.");
                return false;
            }
            
            ConsoleUi.printlnErr("Неверный выбор. Попробуйте снова.");
            return false;
        }
        
        ConsoleUi.printlnErr("Критическая ошибка: Архитектурный тип учетной записи не распознан.");
        return true;
    }
}