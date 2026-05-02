package kbtu_oop_project.application.factory;

import kbtu_oop_project.domain.features.user.Admin;
import kbtu_oop_project.domain.features.user.Employee;
import kbtu_oop_project.domain.features.user.Manager;
import kbtu_oop_project.domain.features.user.ResearchStaff;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.value.Role;

public final class UserFactory {

    public User createUser(Role role) {
        return switch (role) {
            case STUDENT -> new Student();
            case TEACHER -> new Teacher();
            case EMPLOYEE -> new Employee();
            case ADMIN -> new Admin();
            case MANAGER -> new Manager();
            case RESEARCH_STAFF -> new ResearchStaff();
        };
    }
}
