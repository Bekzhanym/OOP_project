package edu.university.application.factory;

import edu.university.domain.model.Admin;
import edu.university.domain.model.Employee;
import edu.university.domain.model.Manager;
import edu.university.domain.model.ResearchStaff;
import edu.university.domain.model.Student;
import edu.university.domain.model.Teacher;
import edu.university.domain.model.User;
import edu.university.domain.value.Role;

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
