package kbtu_oop_project.console.features.user;

import kbtu_oop_project.domain.features.user.*;

public final class UserRoleFormatter {

    private UserRoleFormatter() {
        throw new UnsupportedOperationException("Это утилитарный класс-форматтер.");
    }

    private static User extractCoreUser(User u) {
        if (u == null) return null;
        if (u.getClass().getSimpleName().contains("Researcher")) {
            try {
                var method = u.getClass().getMethod("getOriginalUser");
                return (User) method.invoke(u);
            } catch (Exception e) {
                return u;
            }
        }
        return u;
    }

    public static String describe(User u) {
        if (u == null) {
            return "Неизвестный пользователь";
        }

        User coreUser = extractCoreUser(u);
        
        String prefix = u.getClass().getSimpleName().contains("Researcher") ? "[Исследователь] " : "";

        String role;
        if (coreUser instanceof Admin) {
            role = "Администратор";
        } else if (coreUser instanceof Manager) {
            role = "Менеджер Office of Registrar";
        } else if (coreUser instanceof ResearchStaff) {
            role = "Научный сотрудник";
        } else if (coreUser instanceof Professor) {
            role = "Профессор";
        } else if (coreUser instanceof Teacher) {
            role = "Преподаватель";
        } else if (coreUser instanceof Student4thYear) {
            role = "Студент (Выпускной курс)";
        } else if (coreUser instanceof Student) {
            role = "Студент";
        } else {
            role = coreUser.getClass().getSimpleName();
        }
        return prefix + role;
    }
}