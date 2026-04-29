package edu.university.application.usecase;

import edu.university.domain.model.Course;
import edu.university.domain.model.Student;

public final class RegisterStudentForCourseUseCase {

    public void execute(Student student, Course course) {
        student.registerForCourse(course);
    }
}
