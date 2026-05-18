package kbtu_oop_project;

import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

public final class UniversityApp {

    private static final UniversityDatabase DATABASE = UniversityDatabase.getInstance();

    private UniversityApp() {
    }

    public static UniversityDatabase db() {
        return DATABASE;
    }
}
