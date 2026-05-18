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

    public Teacher() {
        super();
    }

    public Teacher(String id, String firstName, String lastName, String email, String password, TeacherTitle title) {
        super(id, firstName, lastName, email, password);
        setTeacherTitle(title);
    }

    private List<Course> taughtCoursesBacking() {
        if (taughtCourses == null) taughtCourses = new ArrayList<>();
        return taughtCourses;
    }

    private Map<String, Integer> receivedRatingsBacking() {
        if (receivedRatings == null) receivedRatings = new HashMap<>();
        return receivedRatings;
    }

    public boolean isResearcher() {
        return this.researcherProfile != null;
    }

    private ResearcherProfile ensureProfileExists() {
        if (this.researcherProfile == null) {
            this.researcherProfile = new ResearcherProfile();
        }
        return this.researcherProfile;
    }

    @Override
    public int getHIndex() {
        return isResearcher() ? this.researcherProfile.getHIndex() : 0;
    }

    @Deprecated
    public void setHIndex(int hIndex) {

        
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return isResearcher() ? this.researcherProfile.getPapers() : Collections.emptyList();
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        ensureProfileExists().addPaper(paper);
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        if (!isResearcher()) {
            System.out.println("Данный преподаватель пока не опубликовал научных трудов.");
            return;
        }
        this.researcherProfile.printPapers(comparator);
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return isResearcher() ? this.researcherProfile.getResearchProjects() : Collections.emptyList();
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        ensureProfileExists().addResearchProject(project);
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
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Оценка должна быть в диапазоне 1-5 звезд.");
        receivedRatingsBacking().put(studentEmail.trim().toLowerCase(Locale.ROOT), stars);
    }

    public Map<String, Integer> getReceivedRatings() {
        return Collections.unmodifiableMap(new HashMap<>(receivedRatingsBacking()));
    }

    
    public void putMark(Student student, Course course, Mark mark) {
        Objects.requireNonNull(student, "Студент обязателен.");
        Objects.requireNonNull(course, "Курс обязателен.");
        Objects.requireNonNull(mark, "Оценка обязательна.");

        if (!taughtCoursesBacking().contains(course)) {
            throw new IllegalStateException("Вы не являетесь назначенным преподавателем курса: " + course.getCourseName());
        }

        student.getTranscript().addMark(course, mark);
        System.out.println(String.format("✅ Преподаватель %s выставил оценку для %s по курсу %s", 
                this.getLastName(), student.getFullName(), course.getCourseCode()));
    }

    public void manageCourse(Course course, String newName, Integer newCredits,
                             CourseType newType, Lesson newLesson) {
        Objects.requireNonNull(course, "Курс обязателен");
        if (!taughtCoursesBacking().contains(course)) {
            throw new IllegalStateException("Вы не имеете прав управления данным курсом.");
        }
        if (newName != null && !newName.isBlank()) course.setCourseName(newName.trim());
        if (newCredits != null && newCredits > 0) course.setCredits(newCredits);
        if (newType != null) course.setCourseType(newType);
        if (newLesson != null) course.addLesson(newLesson); 
        course.notifyObservers();
    }

    public void exportGradeReport(Course course) {
        Objects.requireNonNull(course, "Курс обязателен");
        if (!taughtCoursesBacking().contains(course)) {
            throw new IllegalStateException("Доступ запрещен.");
        }
        System.out.println("\n=== ОФИЦИАЛЬНАЯ ВЕДОМОСТЬ КУРСА: " + course.getCourseCode() + " ===");
        if (course.getEnrolledStudents().isEmpty()) {
            System.out.println("(нет зарегистрированных студентов)");
            return;
        }
        for (Student s : course.getEnrolledStudents()) {
            Mark m = s.getTranscript().getMarkForCourse(course.getCourseCode());
            String markStr = m != null ? m.toString() : "No Mark";
            System.out.println(s.getId() + " | " + s.getFullName() + " | " + markStr); 
        }
    }

    public void writeRecommendation(Student student, String customText) {
        Objects.requireNonNull(student, "Студент обязателен");

        String finalSubstance = (customText != null && !customText.isBlank()) ? customText.trim() :
                "Настоящим подтверждаю высокие академические и этические показатели студента " + student.getFullName() + ".";

        RecommendationLetter letter = new RecommendationLetter(this, student.getEmail(), finalSubstance);
        System.out.println("📜 Рекомендательное письмо успешно подписано и отправлено студенту:\n" + letter);
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