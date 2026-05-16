package kbtu_oop_project.console.features.user;

import kbtu_oop_project.domain.features.user.Admin;
import kbtu_oop_project.domain.features.user.Manager;
import kbtu_oop_project.domain.features.user.Professor;
import kbtu_oop_project.domain.features.user.ResearchStaff;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Student4thYear;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;

public final class UserRoleFormatter {

    private UserRoleFormatter() {
    }

    public static String describe(User u) {
        if (u instanceof Admin) {
            return "Администратор";
        }
        if (u instanceof Professor) {
            return "Профессор";
        }
        if (u instanceof Teacher) {
            return "Преподаватель";
        }
        if (u instanceof Manager) {
            return "Менеджер";
        }
        if (u instanceof ResearchStaff) {
            return "Научный сотрудник";
        }
        if (u instanceof Student4thYear) {
            return "Студент (4 курс)";
        }
        if (u instanceof Student) {
            return "Студент";
        }
        return u.getClass().getSimpleName();
    }
}
