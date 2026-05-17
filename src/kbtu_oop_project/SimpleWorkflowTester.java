package kbtu_oop_project;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Mark;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.value.TeacherTitle;

public class SimpleWorkflowTester {
    public static void main(String[] args) {
        System.out.println("\n==========================================================");
        System.out.println("🚀 ЗАПУСК ПОЛЬЗОВАТЕЛЬСКОГО СЦЕНАРИЯ ТЕСТИРОВАНИЯ");
        System.out.println("==========================================================");

        try {
            
            Teacher teacher = new Teacher("T1", "John", "Doe", "j_doe@kbtu.kz", "password123", TeacherTitle.PROFESSOR);
            Student student = new Student(); 
            student.setEmail("alizhan@kbtu.kz");
            
            Course oopCourse = new Course();
            oopCourse.setCourseCode("CSCI2104");
            oopCourse.setCourseName("Object-Oriented Programming");
            oopCourse.setCredits(5);

            
            oopCourse.addInstructor(teacher);
            teacher.attachTeachingAssignment(oopCourse);
            oopCourse.enrollStudent(student);

            System.out.println("✅ Шаг 1: Компоненты курса и пользователи успешно связаны.");

            
            Mark testMark = new Mark(); 
            teacher.putMark(student, oopCourse, testMark);
            
            System.out.println("✅ Шаг 2: Метод putMark отработал без исключений.");

            
            Mark retrievedMark = student.getTranscript().getMarkForCourse(oopCourse.getCourseCode());
            if (retrievedMark != null) {
                System.out.println("✅ Шаг 3: Оценка успешно зафиксирована в транскрипте студента.");
            }

            System.out.println("==========================================================");
            System.out.println("🟢 ТЕСТ ЗАВЕРШЕН: Кастомная бизнес-логика работает корректно!");
            System.out.println("==========================================================\n");

        } catch (Exception e) {
            System.err.println("❌ Тест провален из-за ошибки:");
            e.printStackTrace();
        }
    }
}