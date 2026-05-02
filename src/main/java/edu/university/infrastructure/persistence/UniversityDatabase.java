package edu.university.infrastructure.persistence;

import edu.university.domain.model.Course;
import edu.university.domain.model.Log;
import edu.university.domain.model.Teacher;
import edu.university.domain.model.User;
import edu.university.domain.repository.CourseRepository;
import edu.university.domain.repository.UserRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton persistence (“University Database”). Implements repository ports + Java serialization
 * for stored lists (repository ports + serialized snapshot pattern).
 */
public final class UniversityDatabase implements UserRepository, CourseRepository {
    private static final Path DEFAULT_STORAGE = Paths.get("data", "university-state.ser");

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

    /** Writes users, courses, and logs to disk (standard Java serialization). */
    public void saveData() {
        try {
            Files.createDirectories(DEFAULT_STORAGE.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(DEFAULT_STORAGE))) {
                oos.writeObject(new ArrayList<>(users));
                oos.writeObject(new ArrayList<>(courses));
                oos.writeObject(new ArrayList<>(logLines));
                oos.writeObject(new ArrayList<>(logs));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save university state", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        if (!Files.exists(DEFAULT_STORAGE)) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(DEFAULT_STORAGE))) {
            users.clear();
            courses.clear();
            logLines.clear();
            logs.clear();
            users.addAll((List<User>) ois.readObject());
            courses.addAll((List<Course>) ois.readObject());
            logLines.addAll((List<String>) ois.readObject());
            logs.addAll((List<Log>) ois.readObject());
            rebuildTeachingAssociations();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load university state", e);
        }
    }

    /** Restores bidirectional Course ↔ Teacher links after deserialization. */
    private void rebuildTeachingAssociations() {
        for (Course course : courses) {
            for (Teacher instructor : course.getInstructors()) {
                instructor.attachTeachingAssignment(course);
            }
        }
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

    public void addUser(User user) {
        add(user);
    }

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
