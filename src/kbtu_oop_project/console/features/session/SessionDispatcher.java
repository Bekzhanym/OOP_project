package kbtu_oop_project.console.features.session;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.admin.AdminConsole;
import kbtu_oop_project.console.features.employee.GenericEmployeeConsole;
import kbtu_oop_project.console.features.student.StudentConsole;
import kbtu_oop_project.console.features.teacher.TeacherConsole;
import kbtu_oop_project.console.features.user.UserRoleFormatter;
import kbtu_oop_project.domain.features.user.Admin;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.Scanner;

public final class SessionDispatcher {

    private SessionDispatcher() {
    }

    public static void runSessionForUser(User user, UniversityDatabase db, Scanner in) {
        boolean logout = false;
        while (!logout) {
            ConsoleUi.header("Личный кабинет — " + UserRoleFormatter.describe(user));
            System.out.println(user.getFirstName() + " " + user.getLastName()
                    + " <" + user.getEmail() + ">");
            System.out.println();

            if (user instanceof Admin adminUser) {
                logout = AdminConsole.adminMenu(adminUser, db, in);
            } else if (user instanceof Teacher teacher) {
                logout = TeacherConsole.teacherMenu(teacher, in);
            } else if (user instanceof Student student) {
                logout = StudentConsole.studentMenu(student, db, in);
            } else {
                logout = GenericEmployeeConsole.menu(in);
            }
        }
        ConsoleUi.printlnOk("Вы вышли из аккаунта.");
    }
}
