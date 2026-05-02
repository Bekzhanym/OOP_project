package kbtu_oop_project.domain.features.course;

import kbtu_oop_project.domain.features.notification.Notification;
import kbtu_oop_project.domain.features.notification.Observer;
import kbtu_oop_project.domain.features.user.Teacher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Course implements Subject, Serializable {

    private static final long serialVersionUID = 1L;

    private String courseCode;
    private String courseName;
    private int credits;
    private transient List<Observer> observers;
    private final List<Teacher> instructors = new ArrayList<>();
    private Mark templateMark;
    private Lesson lesson;
    private Room room;

    private List<Observer> observersBacking() {
        if (observers == null) {
            observers = new CopyOnWriteArrayList<>();
        }
        return observers;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        observers = new CopyOnWriteArrayList<>();
    }

    public void addInstructor(Teacher teacher) {
        if (teacher != null && !instructors.contains(teacher)) {
            instructors.add(teacher);
            teacher.attachTeachingAssignment(this);
        }
    }

    public void removeInstructor(Teacher teacher) {
        if (teacher != null && instructors.remove(teacher)) {
            teacher.detachTeachingAssignment(this);
        }
    }

    public List<Teacher> getInstructors() {
        return Collections.unmodifiableList(instructors);
    }

    @Override
    public void attach(Observer observer) {
        subscribe(observer);
    }

    @Override
    public void detach(Observer observer) {
        unsubscribe(observer);
    }

    @Override
    public void notifyObservers() {
        notifyObservers(new Notification(
                "Course updated: " + courseName,
                LocalDate.now(),
                false));
    }

    public void subscribe(Observer observer) {
        observersBacking().add(observer);
    }

    public void unsubscribe(Observer observer) {
        observersBacking().remove(observer);
    }

    public void notifyObservers(Notification notification) {
        for (Observer observer : observersBacking()) {
            observer.update(notification);
        }
    }

    @Override
    public String toString() {
        return courseCode + " — " + courseName + " (" + credits + " cr)";
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public List<Observer> getObservers() {
        return observersBacking();
    }

    public Mark getTemplateMark() {
        return templateMark;
    }

    public void setTemplateMark(Mark templateMark) {
        this.templateMark = templateMark;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
