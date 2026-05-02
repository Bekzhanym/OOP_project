package kbtu_oop_project.console.features.user;

import kbtu_oop_project.domain.features.user.Admin;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;

public final class UserRoleFormatter {

    private UserRoleFormatter() {
    }

    public static String describe(User u) {
        if (u instanceof Admin) {
            return "Администратор";
        }
        if (u instanceof Teacher) {
            return "Преподаватель";
        }
        if (u instanceof Student) {
            return "Студент";
        }
        return u.getClass().getSimpleName();
    }
}
