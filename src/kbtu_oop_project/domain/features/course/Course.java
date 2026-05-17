package kbtu_oop_project.domain.features.course;

import kbtu_oop_project.domain.features.notification.Notification;
import kbtu_oop_project.domain.features.notification.Observer;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.value.CourseType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Course implements Subject {

    private static final long serialVersionUID = 1L;

    private String courseCode;
    private String courseName;
    private int credits;
    
    private transient List<Observer> observers;
    
    private List<Teacher> instructors = new ArrayList<>();
    private List<Student> enrolledStudents = new ArrayList<>();
    private Mark templateMark;
    
    private List<Lesson> lessons = new CopyOnWriteArrayList<>();
    private Room room;
    private String intendedMajor;
    private int intendedYearOfStudy;
    private CourseType courseType = CourseType.ELECTIVE;

    private List<Observer> observersBacking() {
        if (observers == null) {
            observers = new CopyOnWriteArrayList<>();
        }
        return observers;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        observers = new CopyOnWriteArrayList<>();
        if (instructors == null) instructors = new ArrayList<>();
        if (enrolledStudents == null) enrolledStudents = new ArrayList<>();
        if (lessons == null) {
            lessons = new CopyOnWriteArrayList<>();
        } else {
            lessons = new CopyOnWriteArrayList<>(lessons);
        }
    }

    public void addInstructor(Teacher teacher) {
        if (teacher == null) return;

        if (!instructors.contains(teacher)) {
            instructors.add(teacher);
            teacher.attachTeachingAssignment(this);
            attach(teacher);
        }
    }

    public void removeInstructor(Teacher teacher) {
        if (teacher == null) return;

        if (instructors.remove(teacher)) {
            teacher.detachTeachingAssignment(this);
            detach(teacher);
        }
    }

    public List<Teacher> getInstructors() {
        return Collections.unmodifiableList(instructors);
    }

    public void enrollStudent(Student student) {
        if (student != null && !enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            attach(student);
        }
    }

    public void removeStudent(Student student) {
        if (student == null) return;
        if (enrolledStudents.remove(student)) {
            detach(student);
        }
    }

    public void clearEnrolledStudents() {
        for (Student student : enrolledStudents) {
            detach(student);
        }
        enrolledStudents.clear();
    }

    public List<Student> getEnrolledStudents() {
        return Collections.unmodifiableList(enrolledStudents);
    }

    
    @Override
    public void attach(Observer observer) {
        if (observer != null && !observersBacking().contains(observer)) {
            observersBacking().add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        if (observer != null) {
            observersBacking().remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        notifyObservers(new Notification(
                "Обновлены параметры курса: " + courseName + " [" + courseCode + "]"));
    }

    public void notifyObservers(Notification notification) {
        if (notification == null) return;
        for (Observer observer : observersBacking()) {
            observer.update(notification);
        }
    }

    
    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    public void addLesson(Lesson targetLesson) {
        if (targetLesson != null && !lessons.contains(targetLesson)) {
            this.lessons.add(targetLesson);
        }
    }

    public void clearLessons() {
        this.lessons.clear();
    }

    @Override
    public String toString() {
        return String.format("%s — %s (%d ECTS, %s)", courseCode, courseName, credits, getCourseType());
    }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public Mark getTemplateMark() { return templateMark; }
    public void setTemplateMark(Mark templateMark) { this.templateMark = templateMark; }
    
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    
    public String getIntendedMajor() { return intendedMajor; }
    public void setIntendedMajor(String intendedMajor) { this.intendedMajor = intendedMajor; }
    
    public int getIntendedYearOfStudy() { return intendedYearOfStudy; }
    public void setIntendedYearOfStudy(int intendedYearOfStudy) { this.intendedYearOfStudy = intendedYearOfStudy; }
    
    public CourseType getCourseType() { return courseType != null ? courseType : CourseType.ELECTIVE; }
    public void setCourseType(CourseType courseType) { this.courseType = courseType != null ? courseType : CourseType.ELECTIVE; }
}