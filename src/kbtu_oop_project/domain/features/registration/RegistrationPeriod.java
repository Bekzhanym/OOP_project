package kbtu_oop_project.domain.features.registration;

import java.time.LocalDateTime;

public enum RegistrationPeriod {
    
    MAIN_REGISTRATION("Основная регистрация", true, false),
    
    ADD_DROP("Период Add/Drop", true, true),
    
    MINOR_SELECTION("Выбор Майнора", false, false),
    
    SUMMER_REGISTRATION("Регистрация на Летний семестр", true, false),
    
    NONE("Регистрация закрыта", false, false);

    private final String displayName;
    private final boolean allowsMajorCourses;
    private final boolean allowsDropping;

    RegistrationPeriod(String displayName, boolean allowsMajorCourses, boolean allowsDropping) {
        this.displayName = displayName;
        this.allowsMajorCourses = allowsMajorCourses;
        this.allowsDropping = allowsDropping;
    }

    public boolean allowsMajorCourses() {
        return allowsMajorCourses;
    }

    public boolean allowsDropping() {
        return allowsDropping;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return String.format("%s (Допуск к Major: %b, Допуск к Drop: %b)", 
                displayName, allowsMajorCourses, allowsDropping);
    }
}