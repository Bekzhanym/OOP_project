package kbtu_oop_project.application.factory;

import kbtu_oop_project.domain.features.user.*;
import kbtu_oop_project.domain.value.ManagerType;
import kbtu_oop_project.domain.value.Role;
import kbtu_oop_project.domain.value.TeacherTitle;

public final class UserFactory {

    public UserFactory() {
        throw new UnsupportedOperationException("This is a utility factory class and cannot be instantiated");
    }

    public static User createUser(Role role) {
        return switch (role) {
            case ADMIN -> new Admin();
            case MANAGER -> new Manager();
            case EMPLOYEE -> new Employee();
            case STUDENT -> { Student s = new Student(); s.setYearOfStudy(1); yield s; }
            case STUDENT_4TH_YEAR -> new Student4thYear();
            case TEACHER -> new Teacher();
            case PROFESSOR -> { Teacher t = new Teacher(); t.setTeacherTitle(TeacherTitle.PROFESSOR); yield t; }
            case RESEARCH_STAFF -> new ResearchStaff();
        };
    }

    public static User createUser(Role role, String id, String firstName, String email, String password) {
        return switch (role) {
            case ADMIN -> new Admin(id, firstName, "", email, password);
            case MANAGER -> new Manager(id, firstName, "", email, password, ManagerType.values()[0]);
            case EMPLOYEE -> new Employee(id, firstName, "", email, password);
            case STUDENT -> new Student(id, firstName, "", email, password, 1);
            case STUDENT_4TH_YEAR -> new Student4thYear(id, firstName, "", email, password);
            case TEACHER -> new Teacher(id, firstName, "", email, password, TeacherTitle.LECTOR);
            case PROFESSOR -> new Teacher(id, firstName, "", email, password, TeacherTitle.PROFESSOR);
            case RESEARCH_STAFF -> new ResearchStaff(id, firstName, "", email, password);
        };
    }
}