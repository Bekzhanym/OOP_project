package kbtu_oop_project;

import kbtu_oop_project.application.factory.UserFactory;
import kbtu_oop_project.application.usecase.AdvancedSearchUseCase;
import kbtu_oop_project.application.usecase.RegisterStudentForCourseUseCase;
import kbtu_oop_project.application.usecase.ResearcherDirectoryUseCase;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

/**
 * Одна точка входа для консоли и тестов: здесь создаются/use-case’ы и база.
 * Остальной код по-прежнему разнесён по слоям (domain / application / infrastructure).
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

    public static AdvancedSearchUseCase search() {
        return new AdvancedSearchUseCase(DATABASE, DATABASE);
    }

    public static ResearcherDirectoryUseCase researchers() {
        return new ResearcherDirectoryUseCase(DATABASE);
    }

    public static RegisterStudentForCourseUseCase registration() {
        return new RegisterStudentForCourseUseCase();
    }
}
