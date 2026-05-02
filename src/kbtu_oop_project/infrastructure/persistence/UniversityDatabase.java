package kbtu_oop_project.infrastructure.persistence;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.repository.CourseRepository;
import kbtu_oop_project.domain.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public final class UniversityDatabase implements UserRepository, CourseRepository {
    private static final Path DEFAULT_STORAGE = Paths.get("data", "university-state.ser");

    /** Snapshots saved before packages moved out of {@code edu.university.domain.model}. */
    private static final Map<String, String> LEGACY_MODEL_TO_FEATURES = legacyModelAliases();

    private static Map<String, String> legacyModelAliases() {
        Map<String, String> m = new HashMap<>();
        String u = "kbtu_oop_project.domain.features.user.";
        String c = "kbtu_oop_project.domain.features.course.";
        String r = "kbtu_oop_project.domain.features.research.";
        String n = "kbtu_oop_project.domain.features.notification.";
        m.put("Admin", u + "Admin");
        m.put("Course", c + "Course");
        m.put("Employee", u + "Employee");
        m.put("Lesson", c + "Lesson");
        m.put("Log", "kbtu_oop_project.domain.features.misc.Log");
        m.put("Manager", u + "Manager");
        m.put("Mark", c + "Mark");
        m.put("Notification", n + "Notification");
        m.put("Observer", n + "Observer");
        m.put("Professor", u + "Professor");
        m.put("RecommendationLetter", u + "RecommendationLetter");
        m.put("ResearchPaper", r + "ResearchPaper");
        m.put("ResearchProject", r + "ResearchProject");
        m.put("ResearchStaff", u + "ResearchStaff");
        m.put("Researcher", r + "Researcher");
        m.put("Room", c + "Room");
        m.put("StartupProject", u + "StartupProject");
        m.put("Student", u + "Student");
        m.put("Student4thYear", u + "Student4thYear");
        m.put("Subject", c + "Subject");
        m.put("Teacher", u + "Teacher");
        m.put("Transcript", c + "Transcript");
        m.put("User", u + "User");
        return Map.copyOf(m);
    }

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
        try (ObjectInputStream ois = new MigratingObjectInputStream(Files.newInputStream(DEFAULT_STORAGE))) {
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

    public boolean removeUser(User user) {
        Objects.requireNonNull(user);
        if (user instanceof Teacher t) {
            for (Course c : new ArrayList<>(courses)) {
                if (c.getInstructors().contains(t)) {
                    c.removeInstructor(t);
                }
            }
        }
        return users.remove(user);
    }

    public boolean removeCourseByCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            return false;
        }
        String code = rawCode.trim();
        return courses.removeIf(c -> code.equalsIgnoreCase(c.getCourseCode()));
    }


    private static final class MigratingObjectInputStream extends ObjectInputStream {

        private static final String LEGACY_MODEL_PREFIX = "edu.university.domain.model.";

        MigratingObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc)
                throws IOException, ClassNotFoundException {
            String name = desc.getName();
            if (name.startsWith(LEGACY_MODEL_PREFIX)) {
                String simple = name.substring(LEGACY_MODEL_PREFIX.length());
                String fqcn = LEGACY_MODEL_TO_FEATURES.get(simple);
                if (fqcn != null) {
                    return Class.forName(fqcn);
                }
            }
            if (name.startsWith("edu.university.")) {
                String candidate = name.replace("edu.university.", "kbtu_oop_project.");
                try {
                    return Class.forName(candidate);
                } catch (ClassNotFoundException ignored) {
                }
            }
            return super.resolveClass(desc);
        }
    }
}
