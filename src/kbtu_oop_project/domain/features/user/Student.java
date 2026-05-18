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

    private double gpa;

    private int yearOfStudy;

    private int totalCredits;

    private int failedAttempts;

    private int hIndex;

    private final List<Course> enrolledCourses = new ArrayList<>();

    private final List<ResearchPaper> papers = new ArrayList<>();

    private final List<ResearchProject> researchProjects = new ArrayList<>();

    private final Transcript transcript = new Transcript();

    private StartupProject startupProject;

    private Map<String, Integer> teacherRatingsByEmail = new HashMap<>();

    public Student() {
        super();
    }

    public Student(String id, String firstName, String lastName, String email, String password, int yearOfStudy) {
        super(id, firstName, lastName, email, password);
        this.yearOfStudy = yearOfStudy;
    }

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
        System.out.println("🎓 Студент " + getEmail() + " вошел в личный кабинет Платонуса.");
    }

    @Override
    public void logout() {
        System.out.println("👋 Студент " + getEmail() + " вышел из системы.");
    }

    
    public void registerForCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Дисциплина не может быть null.");
        }
        if (enrolledCourses.contains(course)) {
            throw new IllegalStateException("Вы уже зарегистрированы на курс: " + course.getCourseName());
        }
        if (totalCredits + course.getCredits() > MAX_CREDITS_PER_TERM) {
            throw new IllegalStateException("Превышен лимит кредитов на семестр. Максимум: " + MAX_CREDITS_PER_TERM);
        }

        enrolledCourses.add(course);
        totalCredits += course.getCredits();

        if (!course.getEnrolledStudents().contains(this)) {
            course.enrollStudent(this);
        }
    }

    public void dropCourse(Course course) {
        if (enrolledCourses.remove(course)) {
            totalCredits = Math.max(0, totalCredits - course.getCredits());
            if (course.getEnrolledStudents().contains(this)) {
                course.removeStudent(this);
            }
        }
    }

    
    public void viewTranscript() {
        if (!transcript.hasMarks()) {
            System.out.println("Академический транскрипт пуст. Оценки за текущий период отсутствуют.");
            return;
        }
        System.out.println("\n======= ОФИЦИАЛЬНЫЙ ТРАНСКРИПТ КБТУ =======");
        for (Map.Entry<String, Mark> e : transcript.getGradesBySemester("_").entrySet()) {
            Mark m = e.getValue();
            System.out.println(e.getKey() + " | Итог: " + String.format(Locale.ROOT, "%.1f", m.calculateFinalScore())
                    + " | Буква: " + m.getLetterGrade() 
                    + " | GPA: " + String.format(Locale.ROOT, "%.2f", m.calculateGPA())
                    + " | Статус: " + (m.isPassed() ? "Зачтено" : "Retake"));
        }
        System.out.println("-------------------------------------------");
        this.gpa = transcript.getTotalGPA();
        System.out.println("Итоговый GPA Университета: " + String.format(Locale.ROOT, "%.2f", this.gpa));
        System.out.println("===========================================");
    }

    public StartupProject submitStartup(String title, String description) {
        StartupProject startup = new StartupProject(title, description);
        this.setStartupProject(startup); 
        return startup;
    }

    public void addFailedAttempt() {
        failedAttempts++;
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            System.out.println("🚨 Критическое предупреждение: Студент " + getFullName() + " отправлен на академический совет комиссии.");
        }
    }
    public void rateTeacher(Teacher teacher, int stars1to5) {
        if (teacher == null || teacher.getEmail() == null) {
            throw new IllegalArgumentException("Указан некорректный преподаватель.");
        }
        if (stars1to5 < 1 || stars1to5 > 5) {
            throw new IllegalArgumentException("Оценка должна быть строго от 1 до 5 звезд.");
        }

        String teacherKey = teacher.getEmail().trim().toLowerCase(Locale.ROOT);
        teacherRatingsBacking().put(teacherKey, stars1to5);

        teacher.addRating(this.getEmail(), stars1to5);
        System.out.println("✅ Ваша оценка (" + stars1to5 + "★) для " + teacher.getFullName() + " успешно сохранена.");
    }

    @Override
    public int getHIndex() {
        return hIndex;
    }

    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return Collections.unmodifiableList(papers);
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
            updateHIndex(); 
        }
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        if (papers.isEmpty()) {
            System.out.println("У студента пока нет опубликованных научных трудов.");
            return;
        }
        List<ResearchPaper> copy = new ArrayList<>(papers);
        copy.sort(comparator);

        System.out.println("\n--- НАУЧНЫЕ ПУБЛИКАЦИИ СТУДЕНТА ---");
        copy.forEach(paper -> System.out.println(paper.getDetails()));
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return Collections.unmodifiableList(researchProjects);
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        if (project == null) return;
        if (!researchProjects.contains(project)) {
            researchProjects.add(project);

            if (!project.getParticipants().contains(this)) {
                project.addParticipant(this);
            }
        }
    }

    private void updateHIndex() {
        List<ResearchPaper> sorted = new ArrayList<>(papers);
        sorted.sort((p1, p2) -> Integer.compare(p2.getCitations(), p1.getCitations()));
        int h = 0;
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getCitations() >= (i + 1)) h = i + 1;
            else break;
        }
        this.hIndex = h;
    }

    public String getStudentId() {
        return getId();
    }

    public void setStudentId(String studentId) {

    }

    public double getGpa() {
        if (transcript.hasMarks()) {
            this.gpa = transcript.getTotalGPA();
        }
        return gpa;
    }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public List<Course> getEnrolledCourses() { return Collections.unmodifiableList(enrolledCourses); }
    public int getTotalCredits() { return totalCredits; }
    public int getFailedAttempts() { return failedAttempts; }

    public Transcript getTranscript() { 
        return this.transcript; 
    }

    public StartupProject getStartupProject() { return startupProject; }

    public void setStartupProject(StartupProject project) {
        if (this.startupProject == project) return;
        StartupProject oldProject = this.startupProject;
        this.startupProject = project;
        if (oldProject != null) oldProject.removeTeamMember(this);
        if (project != null && !project.getTeam().contains(this)) {
            project.addTeamMember(this);
        }
    }

    public Map<String, Integer> getTeacherRatingsSnapshot() {
        return Collections.unmodifiableMap(new HashMap<>(teacherRatingsBacking()));
    }

    public String getMajor() { return "Computer Science (Information Systems)"; }
}