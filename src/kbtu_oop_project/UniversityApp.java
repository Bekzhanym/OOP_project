package kbtu_oop_project;

import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;
import kbtu_oop_project.application.factory.UserFactory;
import kbtu_oop_project.domain.features.user.*;
import kbtu_oop_project.domain.value.Role;

public final class UniversityApp {

    private static final UniversityDatabase DATABASE = UniversityDatabase.getInstance();
    private static boolean testMode = false;

    private UniversityApp() {
    }

    public static UniversityDatabase db() {
        return DATABASE;
    }

    public static void enableTestMode() {
        testMode = true;
        System.out.println("[SYSTEM] Test mode enabled. Initializing mock data...");
        initializeSystemData();
    }

    public static void initializeSystemData() {
        if (!DATABASE.getUsers().isEmpty()) {
            return;
        }

        User admin = UserFactory.createUser(Role.ADMIN, "A-01", "Alizhan", "admin@kbtu.kz", "admin123");
        DATABASE.addUser(admin);

        User professor = UserFactory.createUser(Role.PROFESSOR, "P-01", "Dr. Alan", "alan@kbtu.kz", "prof123");
        DATABASE.addUser(professor);

        User student1 = UserFactory.createUser(Role.STUDENT, "S-01", "Askar", "askar@kbtu.kz", "stud123");
        User student2 = UserFactory.createUser(Role.STUDENT_4TH_YEAR, "S-04", "Diyar", "diyar@kbtu.kz", "stud456");
        
        DATABASE.addUser(student1);
        DATABASE.addUser(student2);

        System.out.println("[SYSTEM] Database successfully initialized with " + DATABASE.getUsers().size() + " test users.");
    }

    public static boolean isTestMode() {
        return testMode;
    }
}