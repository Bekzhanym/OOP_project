package kbtu_oop_project.domain.features.course;

import kbtu_oop_project.domain.features.notification.Notification;
import kbtu_oop_project.domain.features.notification.Observer;

import java.io.Serializable;

public interface Subject extends Serializable {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
    void notifyObservers(Notification notification);
}
