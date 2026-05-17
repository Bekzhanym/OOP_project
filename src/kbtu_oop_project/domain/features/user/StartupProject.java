package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.value.StartupStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StartupProject implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private final List<Student> team = new ArrayList<>();
    private StartupStatus status = StartupStatus.UNDER_REVIEW;

    public StartupProject() {
    }

    public StartupProject(String name, String description) {
        this.name = Objects.requireNonNullElse(name, "").trim();
        this.description = Objects.requireNonNullElse(description, "").trim();
        this.status = StartupStatus.UNDER_REVIEW;
    }

    public void submitStartup(String title, String description) {
        this.name = title;
        this.description = description;
        this.status = StartupStatus.UNDER_REVIEW;
    }

    public void addTeamMember(Student student) {
        if (student != null && !team.contains(student)) {
            team.add(student);
        }
    }

    public void removeTeamMember(Student student) {
        if (student != null) {
            team.remove(student);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🚀 СТАРТАП-ПРОЕКТ: %s [%s]\n", name, status));
        sb.append(String.format("Описание: %s\n", (description == null || description.isBlank()) ? "—" : description));
        sb.append("Состав команды:\n");
        if (team.isEmpty()) {
            sb.append("  — Команда пуста\n");
        } else {
            for (Student student : team) {
                sb.append(String.format("  • %s (%s) — Специальность: %s\n", 
                        student.getFullName(), student.getEmail(), student.getMajor()));
            }
        }
        return sb.toString();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Student> getTeam() { 
        return Collections.unmodifiableList(team); 
    }

    public StartupStatus getStatus() { return status; }
    public void setStatus(StartupStatus status) { this.status = status; }
}