package kbtu_oop_project.console.features.user;

import kbtu_oop_project.domain.features.user.*;

public final class UserRoleFormatter {

    private UserRoleFormatter() {
    }

    public static String describe(User u) {
        if (u == null) {
            return "Неизвестный пользователь";
        }
        
        return switch (u) {
            case Admin a         -> "Администратор";
            case Manager m       -> "Менеджер";
            case ResearchStaff r -> "Научный сотрудник";
            case Professor p     -> "Профессор";
            case Teacher t       -> "Преподаватель";
            case Student4thYear s4 -> "Студент (4 курс)";
            case Student s       -> "Студент";
            default              -> u.getClass().getSimpleName();
        };
    }
}