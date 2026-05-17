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

import java.time.LocalDate;
import java.util.Scanner;

public final class SessionDispatcher {

    private SessionDispatcher() {
    }

    public static void runSessionForUser(User user, UniversityDatabase db, Scanner in) {
        boolean logout = false;
        while (!logout) {
            ConsoleUi.header("Личный кабинет — " + UserRoleFormatter.describe(user));
            System.out.println("  Пользователь: " + user.getFirstName() + " " + user.getLastName());
            System.out.println("  Email: <" + user.getEmail() + ">");
            System.out.println("────────────────────────────────────────────────");

            logout = dispatchSession(user, db, in);
            
            if (logout) {
                db.recordAudit("LOGOUT " + safeEmail(user));
                Log lg = new Log();
                lg.setAction("logout");
                lg.setUserId(user.getId());
                lg.setTimestamp(LocalDate.now());
                db.recordStructured(lg);
            }
        }
        ConsoleUi.printlnOk("Вы успешно вышли из системы.");
    }

    private static String safeEmail(User user) {
        return user.getEmail() != null ? user.getEmail() : "(no-email)";
    }

    private static boolean dispatchSession(User user, UniversityDatabase db, Scanner in) {
        if (user instanceof Admin adminUser) {
            return AdminConsole.adminMenu(adminUser, db, in);
        }
        
        if (user instanceof Teacher teacher) {
            return TeacherConsole.teacherMenu(teacher, db, in);
        }
        
        if (user instanceof Student student) {
            return StudentConsole.studentMenu(student, db, in);
        }
        
        if (user instanceof Manager manager) {
            return ManagerConsole.managerMenu(manager, db, in);
        }
        
        if (user instanceof Employee employee) {
            GenericEmployeeConsole.start(employee, db, in);
            
            System.out.print("Выйти из системы? (y/n): ");
            return "y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()));
        }
        
        ConsoleUi.printlnErr("Критическая ошибка: Архитектурный тип учетной записи не распознан.");
        return true;
    }
}