package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.value.StartupStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StartupProject implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private final List<Student> team = new ArrayList<>();
    private StartupStatus status = StartupStatus.UNDER_REVIEW;

    public void submitStartup(String title) {
        this.name = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Student> getTeam() {
        return team;
    }

    public StartupStatus getStatus() {
        return status;
    }

    public void setStatus(StartupStatus status) {
        this.status = status;
    }
}
