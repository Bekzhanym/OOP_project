package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Mark;
import kbtu_oop_project.domain.features.course.Transcript;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.research.Researcher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Student extends User implements Researcher {

    private static final long serialVersionUID = 1L;
    private static final int MAX_CREDITS_PER_TERM = 21;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    private String studentId;
    private double gpa;
    private int yearOfStudy;
    private final List<Course> enrolledCourses = new ArrayList<>();
    private int totalCredits;
    private int failedAttempts;
    private final Transcript transcript = new Transcript();
    private int hIndex;
    private final List<ResearchPaper> papers = new ArrayList<>();
    private final List<ResearchProject> researchProjects = new ArrayList<>();
    /** Non-final: older .ser snapshots may deserialize this map as null. */
    private Map<String, Integer> teacherRatingsByEmail = new HashMap<>();

    private Map<String, Integer> teacherRatingsBacking() {
        if (teacherRatingsByEmail == null) {
            teacherRatingsByEmail = new HashMap<>();
        }
        return teacherRatingsByEmail;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (teacherRatingsByEmail == null) {
            teacherRatingsByEmail = new HashMap<>();
        }
    }

    @Override
    public void login() {
    }

    @Override
    public void logout() {
    }

    @Override
    public void changePassword(String newPassword) {
        setPassword(newPassword);
    }

    public void registerForCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course required");
        }
        if (enrolledCourses.contains(course)) {
            throw new IllegalStateException("Already enrolled in this course.");
        }
        if (totalCredits + course.getCredits() > MAX_CREDITS_PER_TERM) {
            throw new IllegalStateException("Would exceed max credits per term (" + MAX_CREDITS_PER_TERM + ").");
        }
        enrolledCourses.add(course);
        totalCredits += course.getCredits();
        course.enrollStudent(this);
    }

    /** Вывод transcript в stdout (как в UML: операция estudента). */
    public void viewTranscript() {
        if (!transcript.hasMarks()) {
            System.out.println("Пока нет сохранённых оценок.");
            return;
        }
        for (Map.Entry<String, Mark> e : transcript.getGradesBySemester("_").entrySet()) {
            Mark m = e.getValue();
            System.out.println(e.getKey() + ": итог=" + String.format(Locale.ROOT, "%.1f", m.calculateFinalScore())
                    + ", GPA(ball)=" + String.format(Locale.ROOT, "%.2f", m.calculateGPA())
                    + ", зачёт=" + (m.isPassed() ? "да" : "нет"));
        }
        System.out.println("Средний GPA по transcript: "
                + String.format(Locale.ROOT, "%.2f", transcript.getTotalGPA()));
    }

    public void submitStartup(String title) {
    }

    public void requestRecommendation(Teacher teacher) {
        teacher.writeRecommendation(this);
    }

    public void addFailedAttempt() {
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            throw new IllegalStateException("max failed attempts");
        }
        failedAttempts++;
    }

    @Override
    public int getHIndex() {
        return hIndex;
    }

    @Override
    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return papers;
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> copy = new ArrayList<>(papers);
        copy.sort(comparator);
        for (ResearchPaper paper : copy) {
            System.out.println(paper.getDetails());
        }
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        papers.add(paper);
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return researchProjects;
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        if (project != null && !researchProjects.contains(project)) {
            researchProjects.add(project);
            project.addParticipant(this);
        }
    }

    public void rateTeacher(Teacher teacher, int stars1to5) {
        if (teacher == null || teacher.getEmail() == null || teacher.getEmail().isBlank()) {
            throw new IllegalArgumentException("Teacher email required.");
        }
        if (stars1to5 < 1 || stars1to5 > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }
        teacherRatingsBacking().put(teacher.getEmail().trim().toLowerCase(Locale.ROOT), stars1to5);
    }

    public Map<String, Integer> getTeacherRatingsSnapshot() {
        return Collections.unmodifiableMap(new HashMap<>(teacherRatingsBacking()));
    }

    /** Academic identifier used for transcripts and grading (UML {@code studentId}). */
    public String getStudentId() {
        return studentId != null && !studentId.isBlank() ? studentId : getId();
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Transcript getTranscript() {
        return transcript;
    }
}
