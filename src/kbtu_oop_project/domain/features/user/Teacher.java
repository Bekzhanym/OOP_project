package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Lesson;
import kbtu_oop_project.domain.features.course.Mark;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.research.Researcher;
import kbtu_oop_project.domain.value.CourseType;
import kbtu_oop_project.domain.value.TeacherTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Teacher extends Employee implements Researcher {

    private static final long serialVersionUID = 1L;

    private String department;
    private transient List<Course> taughtCourses;
    private TeacherTitle title;
    private int hIndex;
    private final List<ResearchPaper> papers = new ArrayList<>();
    private final List<ResearchProject> researchProjects = new ArrayList<>();

    private List<Course> taughtCoursesBacking() {
        if (taughtCourses == null) {
            taughtCourses = new ArrayList<>();
        }
        return taughtCourses;
    }

    public void attachTeachingAssignment(Course course) {
        internalRegisterCourse(course);
    }

    public void detachTeachingAssignment(Course course) {
        internalUnregisterCourse(course);
    }

    void internalRegisterCourse(Course course) {
        if (course != null && !taughtCoursesBacking().contains(course)) {
            taughtCoursesBacking().add(course);
        }
    }

    void internalUnregisterCourse(Course course) {
        taughtCoursesBacking().remove(course);
    }

    /**
     * Records an assessment for an enrolled student on a course this teacher instructs.
     */
    public void putMark(Student student, Course course, Mark mark) {
        Objects.requireNonNull(student);
        Objects.requireNonNull(course);
        Objects.requireNonNull(mark);
        if (!getTaughtCourses().contains(course)) {
            throw new IllegalStateException("Teacher does not instruct this course.");
        }
        if (!student.getEnrolledCourses().contains(course)) {
            throw new IllegalStateException("Student is not enrolled in this course.");
        }
        mark.calculateFinalScore();
        student.getTranscript().addMark(course.getCourseCode(), mark);
    }

    /**
     * Обновление параметров курса преподавателем (UML {@code manageCourse}).
     */
    public void manageCourse(Course course, String newName, Integer credits,
                             CourseType courseType, Lesson lessonReplacement) {
        Objects.requireNonNull(course);
        if (!getTaughtCourses().contains(course)) {
            throw new IllegalStateException("Teacher does not instruct this course.");
        }
        if (newName != null && !newName.isBlank()) {
            course.setCourseName(newName.trim());
        }
        if (credits != null && credits > 0) {
            course.setCredits(credits);
        }
        if (courseType != null) {
            course.setCourseType(courseType);
        }
        if (lessonReplacement != null) {
            course.setLesson(lessonReplacement);
        }
        course.notifyObservers();
    }

    public void writeRecommendation(Student student) {
        Objects.requireNonNull(student);
        System.out.println("[RecommendationLetter] " + getFirstName() + " " + getLastName()
                + " recommends student " + student.getStudentId() + " (" + student.getEmail() + ")");
    }

    /** Простая статистика по оценкам на курсе (бонус ТЗ — отчёт преподавателя). */
    public void exportGradeReport(Course course) {
        Objects.requireNonNull(course);
        if (!getTaughtCourses().contains(course)) {
            throw new IllegalStateException("Teacher does not instruct this course.");
        }
        System.out.println("=== Отчёт по оценкам: " + course.getCourseCode() + " — " + course.getCourseName() + " ===");
        List<Double> finals = new ArrayList<>();
        int passed = 0;
        for (Student st : course.getEnrolledStudents()) {
            Mark m = st.getTranscript().getMarkForCourse(course.getCourseCode());
            if (m == null) {
                System.out.println(st.getStudentId() + " | нет оценки");
                continue;
            }
            double fs = m.calculateFinalScore();
            finals.add(fs);
            if (m.isPassed()) {
                passed++;
            }
            System.out.println(st.getStudentId() + " | итог=" + String.format("%.1f", fs)
                    + " | зачёт=" + (m.isPassed() ? "да" : "нет"));
        }
        if (finals.isEmpty()) {
            System.out.println("(нет выставленных оценок)");
            return;
        }
        double avg = finals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        System.out.println("Средний итог: " + String.format("%.2f", avg));
        System.out.println("Зачётов: " + passed + " / " + finals.size());
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Course> getTaughtCourses() {
        return Collections.unmodifiableList(taughtCoursesBacking());
    }

    public TeacherTitle getTitle() {
        return title;
    }

    public void setTitle(TeacherTitle title) {
        this.title = title;
    }
}
