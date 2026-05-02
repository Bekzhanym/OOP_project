package kbtu_oop_project.domain.features.course;

import kbtu_oop_project.domain.features.notification.Observer;

public interface Subject {
    void attach(Observer observer);

    void detach(Observer observer);

    void notifyObservers();
}
