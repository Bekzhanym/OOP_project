package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Lesson;
import kbtu_oop_project.domain.features.course.Mark;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.research.Researcher;
import kbtu_oop_project.domain.features.research.ResearcherProfile;
import kbtu_oop_project.domain.value.CourseType;
import kbtu_oop_project.domain.value.TeacherTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class Teacher extends Employee implements Researcher {

    private static final long serialVersionUID = 1L;

    private TeacherTitle teacherTitle;
    private String department;
    private ResearcherProfile researcherProfile;
    private List<Course> taughtCourses;
    private Map<String, Integer> receivedRatings;

    private List<Course> taughtCoursesBacking() {
        if (taughtCourses == null) taughtCourses = new ArrayList<>();
        return taughtCourses;
    }

    private Map<String, Integer> receivedRatingsBacking() {
        if (receivedRatings == null) receivedRatings = new HashMap<>();
        return receivedRatings;
    }

    private ResearcherProfile getProfile() {
        if (researcherProfile == null && teacherTitle == TeacherTitle.PROFESSOR) {
            researcherProfile = new ResearcherProfile();
        }
        return researcherProfile;
    }

    public boolean isResearcher() {
        return getProfile() != null;
    }

    @Override
    public int getHIndex() {
        return isResearcher() ? getProfile().getHIndex() : 0;
    }

    @Override
    public void setHIndex(int hIndex) {
        if (!isResearcher() && teacherTitle != TeacherTitle.PROFESSOR) {
            this.researcherProfile = new ResearcherProfile();
        }
        getProfile().setHIndex(hIndex);
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return isResearcher() ? getProfile().getPapers() : Collections.emptyList();
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        if (!isResearcher()) {
            this.researcherProfile = new ResearcherProfile();
        }
        getProfile().addPaper(paper);
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        if (!isResearcher()) {
            System.out.println("Данный преподаватель не занимается научной деятельностью.");
            return;
        }
        getProfile().printPapers(comparator);
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return isResearcher() ? getProfile().getResearchProjects() : Collections.emptyList();
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        if (!isResearcher()) {
            this.researcherProfile = new ResearcherProfile();
        }
        getProfile().addResearchProject(project);
    }

    public List<Course> getTaughtCourses() {
        return Collections.unmodifiableList(taughtCoursesBacking());
    }

    public void attachTeachingAssignment(Course course) {
        if (course != null && !taughtCoursesBacking().contains(course)) {
            taughtCoursesBacking().add(course);
        }
    }

    public void detachTeachingAssignment(Course course) {
        taughtCoursesBacking().remove(course);
    }

    public void addRating(String studentEmail, int stars) {
        if (studentEmail == null || studentEmail.isBlank()) return;
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Rating must be 1–5.");
        receivedRatingsBacking().put(studentEmail.trim().toLowerCase(Locale.ROOT), stars);
    }

    public Map<String, Integer> getReceivedRatings() {
        return Collections.unmodifiableMap(new HashMap<>(receivedRatingsBacking()));
    }

    public void putMark(Student student, Course course, Mark mark) {
        Objects.requireNonNull(student, "Student required");
        Objects.requireNonNull(course, "Course required");
        Objects.requireNonNull(mark, "Mark required");
        if (!taughtCoursesBacking().contains(course)) {
            throw new IllegalStateException("Вы не являетесь преподавателем данного курса.");
        }
        student.getTranscript().addMark(course, mark);
    }

    public void manageCourse(Course course, String newName, Integer newCredits,
                             CourseType newType, Lesson newLesson) {
        Objects.requireNonNull(course, "Course required");
        if (!taughtCoursesBacking().contains(course)) {
            throw new IllegalStateException("Вы не являетесь преподавателем данного курса.");
        }
        if (newName != null && !newName.isBlank()) course.setCourseName(newName.trim());
        if (newCredits != null && newCredits > 0) course.setCredits(newCredits);
        if (newType != null) course.setCourseType(newType);
        if (newLesson != null) course.setLesson(newLesson);
        course.notifyObservers();
    }

    public void exportGradeReport(Course course) {
        Objects.requireNonNull(course, "Course required");
        if (!taughtCoursesBacking().contains(course)) {
            throw new IllegalStateException("Вы не являетесь преподавателем данного курса.");
        }
        System.out.println("=== Ведомость: " + course.getCourseCode() + " — " + course.getCourseName() + " ===");
        if (course.getEnrolledStudents().isEmpty()) {
            System.out.println("(нет записавшихся студентов)");
            return;
        }
        for (Student s : course.getEnrolledStudents()) {
            Mark m = s.getTranscript().getMarkForCourse(course.getCourseCode());
            String markStr = m != null ? m.toString() : "—";
            System.out.println(s.getStudentId() + " | " + s.getFullName() + " | " + markStr);
        }
    }

    public void writeRecommendation(Student student) {
        Objects.requireNonNull(student, "Student required");
        String text = "Настоящим подтверждаю высокие академические показатели студента " + student.getFullName() + ".";
        RecommendationLetter letter = new RecommendationLetter(this, student.getEmail(), text);
        System.out.println(letter);
    }

    public TeacherTitle getTitle() { return teacherTitle; }
    public void setTitle(TeacherTitle t) { setTeacherTitle(t); }

    public TeacherTitle getTeacherTitle() { return teacherTitle; }

    public void setTeacherTitle(TeacherTitle teacherTitle) {
        this.teacherTitle = teacherTitle;
        if (teacherTitle == TeacherTitle.PROFESSOR && this.researcherProfile == null) {
            this.researcherProfile = new ResearcherProfile();
        }
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
