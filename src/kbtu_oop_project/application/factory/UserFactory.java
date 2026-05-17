package kbtu_oop_project.application.factory;

import kbtu_oop_project.domain.features.user.*;
import kbtu_oop_project.domain.value.Role;
import kbtu_oop_project.domain.value.TeacherTitle; 

public final class UserFactory {

    public UserFactory() {
    }

    public static User createUser(Role role) {
        return switch (role) {
            case ADMIN -> new Admin();
            case MANAGER -> new Manager();
            case EMPLOYEE -> new Employee();
            
            case STUDENT -> {
                Student student = new Student();
                student.setYearOfStudy(1); 
                yield student;
            }
            
            case STUDENT_4TH_YEAR -> new Student4thYear();
            
            case TEACHER -> new Teacher(); 
            
            case PROFESSOR -> {
                Teacher professor = new Teacher();
                professor.setTeacherTitle(TeacherTitle.PROFESSOR); // Используем сеттер титула из Teacher
                yield professor;
            }
            
            case RESEARCH_STAFF -> new ResearchStaff();
        };
    }
}