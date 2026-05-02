package kbtu_oop_project.console.features.student;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.Optional;
import java.util.Scanner;

public final class StudentConsole {

    private StudentConsole() {
    }

    public static boolean studentMenu(Student student, UniversityDatabase db, Scanner in) {
        System.out.println("  1 — Мой профиль и кредиты");
        System.out.println("  2 — Записаться на курс по коду");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                ConsoleUi.header("Профиль студента");
                System.out.println("Кредиты за семестр (учтённые): " + student.getTotalCredits());
                System.out.println("Записан на курсов: " + student.getEnrolledCourses().size());
                for (Course c : student.getEnrolledCourses()) {
                    System.out.println("   • " + c.getCourseCode() + " — " + c.getCourseName());
                }
                break;
            case "2":
                System.out.print("Код курса: ");
                String code = ConsoleUi.trim(in.nextLine());
                Optional<Course> course = db.getCourses().stream()
                        .filter(c -> c.getCourseCode() != null && c.getCourseCode().equalsIgnoreCase(code))
                        .findFirst();
                if (course.isEmpty()) {
                    ConsoleUi.printlnErr("Курс не найден.");
                    break;
                }
                try {
                    student.registerForCourse(course.get());
                    ConsoleUi.printlnOk("Запись успешна. Всего кредитов: " + student.getTotalCredits());
                } catch (IllegalStateException ex) {
                    ConsoleUi.printlnErr(ex.getMessage());
                }
                break;
            case "0":
                return true;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
        return false;
    }
}
