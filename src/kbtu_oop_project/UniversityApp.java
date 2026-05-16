package kbtu_oop_project;

import kbtu_oop_project.application.factory.UserFactory;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

/**
 * Одна точка входа для консоли: singleton-база и фабрика пользователей.
 */
public final class UniversityApp {

    private static final UniversityDatabase DATABASE = UniversityDatabase.getInstance();

    private UniversityApp() {
    }

    public static UniversityDatabase db() {
        return DATABASE;
    }

    public static UserFactory users() {
        return new UserFactory();
    }
}
