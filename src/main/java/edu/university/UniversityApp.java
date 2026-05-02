package edu.university;

import edu.university.application.factory.UserFactory;
import edu.university.application.usecase.AdvancedSearchUseCase;
import edu.university.application.usecase.RegisterStudentForCourseUseCase;
import edu.university.application.usecase.ResearcherDirectoryUseCase;
import edu.university.infrastructure.persistence.UniversityDatabase;

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
