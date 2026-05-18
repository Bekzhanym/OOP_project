package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.value.ManagerType;
import kbtu_oop_project.domain.value.Role;
import kbtu_oop_project.domain.value.TeacherTitle;

public final class UserRoleAssignment {

    private UserRoleAssignment() {
    }

    public static User apply(User source, Role role, int studentYear,
                             ManagerType managerType, TeacherTitle teacherTitle) {
        PendingUser bridge = new PendingUser(
                source.getId(),
                source.getFirstName(),
                source.getLastName(),
                source.getEmail(),
                source.internalPassword());
        return bridge.assignRole(role, studentYear, managerType, teacherTitle);
    }
}
