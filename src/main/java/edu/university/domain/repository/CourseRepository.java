package edu.university.domain.repository;

import edu.university.domain.model.Course;

import java.util.List;

public interface CourseRepository {
    void add(Course course);

    List<Course> findAllCourses();
}
