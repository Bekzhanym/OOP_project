package edu.university.infrastructure.persistence;

import edu.university.domain.model.Course;
import edu.university.domain.model.Log;
import edu.university.domain.model.User;
import edu.university.domain.repository.CourseRepository;
import edu.university.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UniversityDatabase implements UserRepository, CourseRepository {
    private static UniversityDatabase instance;

    private final List<User> users = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<String> logLines = new ArrayList<>();
    private final List<Log> logs = new ArrayList<>();

    private UniversityDatabase() {
    }

    public static synchronized UniversityDatabase getInstance() {
        if (instance == null) {
            instance = new UniversityDatabase();
        }
        return instance;
    }

    public void saveData() {
    }

    public void loadData() {
    }

    @Override
    public void add(User user) {
        users.add(user);
    }

    @Override
    public List<User> findAllUsers() {
        return Collections.unmodifiableList(users);
    }

    @Override
    public void add(Course course) {
        courses.add(course);
    }

    @Override
    public List<Course> findAllCourses() {
        return Collections.unmodifiableList(courses);
    }

    /** Legacy helper matching earlier API name. */
    public void addUser(User user) {
        add(user);
    }

    /** Legacy helper matching earlier API name. */
    public void addCourse(Course course) {
        add(course);
    }

    public List<User> getUsers() {
        return findAllUsers();
    }

    public List<Course> getCourses() {
        return findAllCourses();
    }

    public List<String> getLogLines() {
        return Collections.unmodifiableList(logLines);
    }

    public List<Log> getLogs() {
        return Collections.unmodifiableList(logs);
    }
}
