package kbtu_oop_project.infrastructure.persistence;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.misc.EmployeeMessage;
import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.misc.PendingCourseRegistration;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.research.Researcher;
import kbtu_oop_project.domain.features.user.Employee;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.value.MessageKind;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class UniversityDatabase {
    
    private static final Path DEFAULT_STORAGE = Paths.get("data", "university-state.ser");
    private static final Path TEMP_STORAGE = Paths.get("data", "university-state.tmp");

    private static final Map<String, String> LEGACY_MODEL_TO_FEATURES = legacyModelAliases();

    private static Map<String, String> legacyModelAliases() {
        Map<String, String> m = new HashMap<>();
        String u = "kbtu_oop_project.domain.features.user.";
        String c = "kbtu_oop_project.domain.features.course.";
        String r = "kbtu_oop_project.domain.features.research.";
        String n = "kbtu_oop_project.domain.features.notification.";
        m.put("Admin", u + "Admin");
        m.put("Course", c + "Course");
        m.put("CourseType", "kbtu_oop_project.domain.value.CourseType");
        m.put("LessonType", "kbtu_oop_project.domain.value.LessonType");
        m.put("Employee", u + "Employee");
        m.put("Lesson", c + "Lesson");
        m.put("Log", "kbtu_oop_project.domain.features.misc.Log");
        m.put("PendingCourseRegistration", "kbtu_oop_project.domain.features.misc.PendingCourseRegistration");
        m.put("EmployeeMessage", "kbtu_oop_project.domain.features.misc.EmployeeMessage");
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

    private static volatile UniversityDatabase instance;

    private final List<User> users = new CopyOnWriteArrayList<>();
    private final List<Course> courses = new CopyOnWriteArrayList<>();
    private final List<String> logLines = new CopyOnWriteArrayList<>();
    private final List<Log> logs = new CopyOnWriteArrayList<>();
    private final List<PendingCourseRegistration> pendingCourseRegistrations = new CopyOnWriteArrayList<>();
    private final List<ResearchProject> researchProjects = new CopyOnWriteArrayList<>();
    private final List<EmployeeMessage> employeeMessages = new CopyOnWriteArrayList<>();
    private final List<String> newsItems = new CopyOnWriteArrayList<>();

    private UniversityDatabase() {
    }

    public static UniversityDatabase getInstance() {
        UniversityDatabase localInstance = instance;
        if (localInstance == null) {
            synchronized (UniversityDatabase.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UniversityDatabase();
                }
            }
        }
        return localInstance;
    }

    public synchronized void saveData() {
        try {
            Files.createDirectories(DEFAULT_STORAGE.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(TEMP_STORAGE))) {
                oos.writeObject(new ArrayList<>(users));
                oos.writeObject(new ArrayList<>(courses));
                oos.writeObject(new ArrayList<>(logLines));
                oos.writeObject(new ArrayList<>(logs));
                oos.writeObject(new ArrayList<>(pendingCourseRegistrations));
                oos.writeObject(new ArrayList<>(researchProjects));
                oos.writeObject(new ArrayList<>(employeeMessages));
                oos.writeObject(new ArrayList<>(newsItems));
            }
            Files.move(TEMP_STORAGE, DEFAULT_STORAGE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("🚨 Critical error: Failed to safely save university state", e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void loadData() {
        if (!Files.exists(DEFAULT_STORAGE)) {
            return;
        }
        try (ObjectInputStream ois = new MigratingObjectInputStream(Files.newInputStream(DEFAULT_STORAGE))) {
            users.clear();
            courses.clear();
            logLines.clear();
            logs.clear();
            pendingCourseRegistrations.clear();
            researchProjects.clear();
            employeeMessages.clear();
            newsItems.clear();

            users.addAll((List<User>) ois.readObject());
            courses.addAll((List<Course>) ois.readObject());
            logLines.addAll((List<String>) ois.readObject());
            logs.addAll((List<Log>) ois.readObject());
            
            readOptionalSnapshotTail(ois);
            
            rebuildTeachingAssociations();
            rebuildEnrollmentAssociations();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("🚨 Critical error: Failed to parse state file. Possible version mismatch.", e);
        }
    }

    private void rebuildTeachingAssociations() {
        for (Course course : courses) {
            if (course.getInstructors() != null) {
                for (Teacher instructor : course.getInstructors()) {
                    instructor.attachTeachingAssignment(course);
                }
            }
        }
    }

    private void rebuildEnrollmentAssociations() {
        for (Course course : courses) {
            course.clearEnrolledStudents();
        }
        for (User user : users) {
            if (user instanceof Student student) {
                for (Course enrolled : student.getEnrolledCourses()) {
                    enrolled.enrollStudent(student);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void readOptionalSnapshotTail(ObjectInputStream ois) {
        try { pendingCourseRegistrations.addAll((List<PendingCourseRegistration>) ois.readObject()); } catch (Exception ignored) {}
        try { researchProjects.addAll((List<ResearchProject>) ois.readObject()); } catch (Exception ignored) {}
        try { employeeMessages.addAll((List<EmployeeMessage>) ois.readObject()); } catch (Exception ignored) {}
        try { newsItems.addAll((List<String>) ois.readObject()); } catch (Exception ignored) {}
    }

    public void add(User user) {
        if (user != null) users.add(user);
    }

    public List<User> findAllUsers() {
        return Collections.unmodifiableList(users);
    }

    public Optional<User> findByEmailIgnoreCase(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        String needle = email.trim().toLowerCase(Locale.ROOT);
        return users.stream()
                .filter(u -> u.getEmail() != null && needle.equals(u.getEmail().trim().toLowerCase(Locale.ROOT)))
                .findFirst();
    }

    public Optional<Student> findStudentByStudentId(String studentId) {
        if (studentId == null || studentId.isBlank()) return Optional.empty();
        String needle = studentId.trim();
        return users.stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .filter(s -> needle.equalsIgnoreCase(s.getStudentId()))
                .findFirst();
    }

    public void add(Course course) {
        if (course != null) courses.add(course);
    }

    public List<Course> findAllCourses() {
        return Collections.unmodifiableList(courses);
    }

    public Optional<Course> findCourseByCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) return Optional.empty();
        String code = rawCode.trim();
        return courses.stream()
                .filter(c -> c.getCourseCode() != null && code.equalsIgnoreCase(c.getCourseCode()))
                .findFirst();
    }

    public List<Object> advancedSearch(String regex) {
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            List<Object> results = new ArrayList<>();
            for (User user : users) {
                if (user.getEmail() != null && pattern.matcher(user.getEmail()).find()) {
                    results.add(user);
                }
            }
            for (Course course : courses) {
                if (course.getCourseName() != null && pattern.matcher(course.getCourseName()).find()) {
                    results.add(course);
                }
            }
            return results;
        } catch (Exception e) {
            return List.of(); 
        }
    }

    public List<Researcher> getAllResearchers() {
        return users.stream()
                .filter(u -> u instanceof Researcher)
                .map(u -> (Researcher) u)
                .collect(Collectors.toList());
    }

    private static int totalCitations(Researcher researcher) {
        return researcher.getPapers() == null ? 0 : researcher.getPapers().stream().mapToInt(ResearchPaper::getCitations).sum();
    }

    private static int citationsInYear(Researcher researcher, int year) {
        if (researcher.getPapers() == null) return 0;
        return researcher.getPapers().stream()
                .filter(p -> p.getDate() != null && p.getDate().getYear() == year)
                .mapToInt(ResearchPaper::getCitations)
                .sum();
    }

    public Optional<Researcher> findTopResearcherByTotalCitations() {
        return getAllResearchers().stream().max(Comparator.comparingInt(UniversityDatabase::totalCitations));
    }

    public Optional<Researcher> findTopResearcherByCitationsInYear(int year) {
        return getAllResearchers().stream().max(Comparator.comparingInt(r -> citationsInYear(r, year)));
    }

    public void printAllResearchersPapersSorted(Comparator<ResearchPaper> comparator) {
        for (Researcher researcher : getAllResearchers()) {
            System.out.println("=== Papers (" + researcher.getClass().getSimpleName()
                    + ", h-index=" + researcher.getHIndex() + ") ===");
            researcher.printPapers(comparator);
        }
    }

    public void addUser(User user) { add(user); }
    public void addCourse(Course course) { add(course); }
    public List<User> getUsers() { return findAllUsers(); }
    public List<Course> getCourses() { return findAllCourses(); }
    public List<String> getLogLines() { return Collections.unmodifiableList(logLines); }
    public List<Log> getLogs() { return Collections.unmodifiableList(logs); }

    public void recordAudit(String message) {
        logLines.add(LocalDate.now() + " | " + message);
    }

    public void recordStructured(Log entry) {
        logs.add(entry);
    }

    public List<String> getNewsLinesView() {
        return Collections.unmodifiableList(new ArrayList<>(newsItems));
    }

    public void addUniversityNews(String headline) {
        if (headline == null || headline.isBlank()) {
            throw new IllegalArgumentException("News text required.");
        }
        newsItems.add(LocalDate.now() + " | " + headline.trim());
        recordAudit("NEWS " + headline.trim());
    }

    private boolean emailsMatchIgnoreCase(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }

    public Optional<Student> findStudentByEmailIgnoreCase(String email) {
        return findByEmailIgnoreCase(email).filter(u -> u instanceof Student).map(u -> (Student) u);
    }

    public List<PendingCourseRegistration> getPendingCourseRegistrationsView() {
        return Collections.unmodifiableList(new ArrayList<>(pendingCourseRegistrations));
    }

    public void submitCourseRegistrationRequest(Student student, Course course) {
        Objects.requireNonNull(student);
        Objects.requireNonNull(course);
        String email = student.getEmail();
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Student must have email.");
        }
        String code = course.getCourseCode();
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Course code required.");
        }
        if (student.getEnrolledCourses().contains(course)) {
            throw new IllegalStateException("Already enrolled.");
        }
        if (student.getTotalCredits() + course.getCredits() > 21) {
            throw new IllegalStateException("Would exceed max credits (21).");
        }
        boolean dupPending = pendingCourseRegistrations.stream().anyMatch(p ->
                emailsMatchIgnoreCase(p.getStudentEmail(), email) && emailsMatchIgnoreCase(p.getCourseCode(), code));
        if (dupPending) {
            throw new IllegalStateException("Request already pending.");
        }
        pendingCourseRegistrations.add(new PendingCourseRegistration(email.trim(), code.trim()));
        recordAudit("PENDING_REG " + email.trim() + " → " + code.trim());
    }

    private Optional<PendingCourseRegistration> removePendingRegistration(String studentEmail, String courseCode) {
        Optional<PendingCourseRegistration> match = pendingCourseRegistrations.stream()
                .filter(p -> emailsMatchIgnoreCase(p.getStudentEmail(), studentEmail) && emailsMatchIgnoreCase(p.getCourseCode(), courseCode))
                .findFirst();
        match.ifPresent(pendingCourseRegistrations::remove);
        return match;
    }

    public boolean approvePendingCourseRegistration(String studentEmail, String courseCode) {
        Optional<PendingCourseRegistration> pend = removePendingRegistration(studentEmail, courseCode);
        if (pend.isEmpty()) return false;

        Optional<Student> st = findStudentByEmailIgnoreCase(studentEmail);
        Optional<Course> co = findCourseByCode(courseCode);
        if (st.isEmpty() || co.isEmpty()) {
            pendingCourseRegistrations.add(pend.get());
            return false;
        }
        try {
            st.get().registerForCourse(co.get());
            recordAudit("APPROVE_REG " + studentEmail + " → " + courseCode);
            return true;
        } catch (IllegalStateException | IllegalArgumentException ex) {
            pendingCourseRegistrations.add(pend.get());
            return false;
        }
    }

    public boolean rejectPendingCourseRegistration(String studentEmail, String courseCode) {
        boolean removed = removePendingRegistration(studentEmail, courseCode).isPresent();
        if (removed) {
            recordAudit("REJECT_REG " + studentEmail + " → " + courseCode);
        }
        return removed;
    }

    public List<ResearchProject> getResearchProjectsUnmodifiable() {
        return Collections.unmodifiableList(researchProjects);
    }

    public void registerGlobalResearchProject(ResearchProject project) {
        if (project != null && !researchProjects.contains(project)) {
            researchProjects.add(project);
        }
    }

    public Optional<ResearchProject> findResearchProjectByTopicSubstring(String needle) {
        if (needle == null || needle.isBlank()) return Optional.empty();
        String n = needle.trim().toLowerCase(Locale.ROOT);
        return researchProjects.stream()
                .filter(p -> p.getTopic() != null && p.getTopic().toLowerCase(Locale.ROOT).contains(n))
                .findFirst();
    }

    public List<EmployeeMessage> messagesForRecipientEmailIgnoreCase(String email) {
        if (email == null || email.isBlank()) return List.of();
        String needle = email.trim().toLowerCase(Locale.ROOT);
        return employeeMessages.stream()
                .filter(m -> m.getToEmail() != null && needle.equals(m.getToEmail().trim().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<EmployeeMessage> messagesRequiringDeanSignature() {
        return employeeMessages.stream()
                .filter(EmployeeMessage::isRequiresDeanSignature)
                .collect(Collectors.toUnmodifiableList());
    }

    public void postEmployeeMessage(User fromUser, String toEmail, MessageKind kind, String body, boolean requiresDeanSignature) {
        Objects.requireNonNull(fromUser);
        if (!(fromUser instanceof Employee)) {
            throw new IllegalArgumentException("Only employees may send internal mail.");
        }
        if (toEmail == null || toEmail.isBlank()) throw new IllegalArgumentException("Recipient email required.");
        if (body == null || body.isBlank()) throw new IllegalArgumentException("Message body required.");

        EmployeeMessage msg = new EmployeeMessage(fromUser.getId(), fromUser.getEmail(), toEmail.trim(), kind, body, requiresDeanSignature);
        employeeMessages.add(msg);
        recordAudit("MAIL_" + kind.name() + " " + fromUser.getEmail() + "→" + toEmail.trim());
    }

    public int getPendingCount() { return pendingCourseRegistrations.size(); }

    public boolean removeUser(User user) {
        Objects.requireNonNull(user);
        if (user instanceof Student st && st.getEmail() != null) {
            pendingCourseRegistrations.removeIf(p -> emailsMatchIgnoreCase(p.getStudentEmail(), st.getEmail()));
        }
        if (user instanceof Teacher t) {
            for (Course c : courses) {
                if (c.getInstructors().contains(t)) {
                    c.removeInstructor(t);
                }
            }
        }
        return users.remove(user);
    }

    public boolean removeCourseByCode(String rawCode) {
        Optional<Course> opt = findCourseByCode(rawCode);
        if (opt.isEmpty()) return false;

        Course victim = opt.get();
        pendingCourseRegistrations.removeIf(p -> emailsMatchIgnoreCase(p.getCourseCode(), victim.getCourseCode()));
        
        for (Teacher t : victim.getInstructors()) {
            victim.removeInstructor(t);
        }
        for (User u : users) {
            if (u instanceof Student s) {
                s.dropCourse(victim);
            }
        }
        victim.clearEnrolledStudents();
        return courses.remove(victim);
    }

    private static final class MigratingObjectInputStream extends ObjectInputStream {
        private static final String LEGACY_MODEL_PREFIX = "edu.university.domain.model.";

        MigratingObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName();
            if (name.startsWith(LEGACY_MODEL_PREFIX)) {
                String simple = name.substring(LEGACY_MODEL_PREFIX.length());
                String fqcn = LEGACY_MODEL_TO_FEATURES.get(simple);
                if (fqcn != null) return Class.forName(fqcn);
            }
            if (name.startsWith("edu.university.")) {
                String candidate = name.replace("edu.university.", "kbtu_oop_project.");
                try {
                    return Class.forName(candidate);
                } catch (ClassNotFoundException ignored) {}
            }
            return super.resolveClass(desc);
        }
    }
}