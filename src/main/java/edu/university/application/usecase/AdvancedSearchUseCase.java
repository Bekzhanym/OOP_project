package edu.university.application.usecase;

import edu.university.domain.model.Course;
import edu.university.domain.model.User;
import edu.university.domain.repository.CourseRepository;
import edu.university.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class AdvancedSearchUseCase {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public AdvancedSearchUseCase(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public List<Object> execute(String regex) {
        Pattern pattern = Pattern.compile(regex);
        List<Object> results = new ArrayList<>();
        for (User user : userRepository.findAllUsers()) {
            if (user.getEmail() != null && pattern.matcher(user.getEmail()).find()) {
                results.add(user);
            }
        }
        for (Course course : courseRepository.findAllCourses()) {
            if (course.getCourseName() != null && pattern.matcher(course.getCourseName()).find()) {
                results.add(course);
            }
        }
        return results;
    }
}
