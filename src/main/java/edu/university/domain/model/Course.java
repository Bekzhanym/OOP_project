package edu.university.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Course implements Subject {
    private String courseCode;
    private String courseName;
    private int credits;
    private final List<Observer> observers = new CopyOnWriteArrayList<>();
    private Mark templateMark;
    private Lesson lesson;
    private Room room;

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
        observers.add(observer);
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Notification notification) {
        for (Observer observer : observers) {
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
        return observers;
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
