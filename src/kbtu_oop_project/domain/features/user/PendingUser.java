package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.value.ManagerType;
import kbtu_oop_project.domain.value.Role;
import kbtu_oop_project.domain.value.TeacherTitle;

import java.util.Objects;

public class PendingUser extends Student {

    private static final long serialVersionUID = 1L;

    public PendingUser(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password, 1);
    }

    @Override
    public void login() {
        System.out.println("Студент " + getEmail() + " вошёл в личный кабинет (1 курс, роль по умолчанию).");
    }

    public User assignRole(Role role, int studentYear, ManagerType managerType, TeacherTitle teacherTitle) {
        Objects.requireNonNull(role, "Роль обязательна.");
        String pwd = internalPassword();

        return switch (role) {
            case STUDENT -> new Student(getId(), getFirstName(), getLastName(), getEmail(), pwd, studentYear);
            case STUDENT_4TH_YEAR -> new Student4thYear(getId(), getFirstName(), getLastName(), getEmail(), pwd);
            case TEACHER -> new Teacher(getId(), getFirstName(), getLastName(), getEmail(), pwd,
                    teacherTitle != null ? teacherTitle : TeacherTitle.LECTOR);
            case PROFESSOR -> new Teacher(getId(), getFirstName(), getLastName(), getEmail(), pwd, TeacherTitle.PROFESSOR);
            case RESEARCH_STAFF -> new ResearchStaff(getId(), getFirstName(), getLastName(), getEmail(), pwd);
            case MANAGER -> new Manager(getId(), getFirstName(), getLastName(), getEmail(), pwd,
                    managerType != null ? managerType : ManagerType.DEPARTMENT);
            case EMPLOYEE -> new Employee(getId(), getFirstName(), getLastName(), getEmail(), pwd);
            case ADMIN -> new Admin(getId(), getFirstName(), getLastName(), getEmail(), pwd);
        };
    }
}
