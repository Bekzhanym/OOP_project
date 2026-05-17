package kbtu_oop_project;

import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;
import kbtu_oop_project.domain.features.user.*;

public final class UniversityApp {

    private static final UniversityDatabase DATABASE = UniversityDatabase.getInstance();

    private UniversityApp() {
    }

    public static UniversityDatabase db() {
        return DATABASE;
    }

    public static void enableTestMode() {
        
    }

    public static void initializeSystemData() {
        
        
        
    }
}