package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.value.ManagerType;

public class Manager extends Employee {
    private ManagerType title;

    public void manageNews() {
    }

    public void approveRegistration() {
    }

    public void generateSchedule() {
    }

    public void createStatisticalReport() {
    }

    public ManagerType getTitle() {
        return title;
    }

    public void setTitle(ManagerType title) {
        this.title = title;
    }
}
