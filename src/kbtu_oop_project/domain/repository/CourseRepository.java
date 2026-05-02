package kbtu_oop_project.domain.repository;

import kbtu_oop_project.domain.features.course.Course;

import java.util.List;

public interface CourseRepository {
    void add(Course course);

    List<Course> findAllCourses();
}
