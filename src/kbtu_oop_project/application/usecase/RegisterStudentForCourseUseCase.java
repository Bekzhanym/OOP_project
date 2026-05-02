package kbtu_oop_project.application.usecase;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.user.Student;

public final class RegisterStudentForCourseUseCase {

    public void execute(Student student, Course course) {
        student.registerForCourse(course);
    }
}
