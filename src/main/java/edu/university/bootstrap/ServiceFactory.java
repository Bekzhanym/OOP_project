package edu.university.bootstrap;

import edu.university.application.factory.UserFactory;
import edu.university.application.usecase.AdvancedSearchUseCase;
import edu.university.application.usecase.RegisterStudentForCourseUseCase;
import edu.university.application.usecase.ResearcherDirectoryUseCase;
import edu.university.infrastructure.persistence.UniversityDatabase;

public final class ServiceFactory {
    private static final UniversityDatabase DATABASE = UniversityDatabase.getInstance();

    private ServiceFactory() {
    }

    public static UniversityDatabase database() {
        return DATABASE;
    }

    public static UserFactory userFactory() {
        return new UserFactory();
    }

    public static AdvancedSearchUseCase advancedSearch() {
        return new AdvancedSearchUseCase(DATABASE, DATABASE);
    }

    public static ResearcherDirectoryUseCase researcherDirectory() {
        return new ResearcherDirectoryUseCase(DATABASE);
    }

    public static RegisterStudentForCourseUseCase registerStudentForCourse() {
        return new RegisterStudentForCourseUseCase();
    }
}
