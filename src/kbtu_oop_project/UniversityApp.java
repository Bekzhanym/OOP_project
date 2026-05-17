package kbtu_oop_project;

import kbtu_oop_project.application.factory.UserFactory;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

public final class UniversityApp {

    private static final UniversityDatabase DATABASE = UniversityDatabase.getInstance();
    
    private static final UserFactory USER_FACTORY = new UserFactory();

    private UniversityApp() {
    }

    public static UniversityDatabase db() {
        return DATABASE;
    }

    public static UserFactory users() {
        return USER_FACTORY;
    }

    public static void initializeSystemData() {
        if (DATABASE.getUsers().isEmpty()) {
            System.out.println("[Система] Обнаружена пустая база данных. Генерация системных аккаунтов по умолчанию...");
            
            
            DATABASE.save(); 
            System.out.println("[Система] Первичные данные успешно развернуты и сериализованы.");
        }
    }
}