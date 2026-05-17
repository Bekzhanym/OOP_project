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
        this.name = name != null && !name.isBlank() ? name.trim() : "Без названия";
        this.description = description != null && !description.isBlank() ? description.trim() : "Описание отсутствует";
        this.status = StartupStatus.UNDER_REVIEW;
    }

    public void sendForReview() {
        if (this.team.isEmpty()) {
            System.out.println("⚠️ Предупреждение: Нельзя отправить стартап на ревью без команды.");
            return;
        }
        this.status = StartupStatus.UNDER_REVIEW;
        System.out.println("🚀 Стартап-проект '" + name + "' успешно отправлен на рассмотрение координаторам КБТУ.");
    }

    public void addTeamMember(Student student) {
        if (student == null) return;

        if (!team.contains(student)) {
            team.add(student);
            
            if (student.getStartupProject() != this) {
                student.setStartupProject(this);
            }
        }
    }

    public void removeTeamMember(Student student) {
        if (student == null) return;

        if (team.contains(student)) {
            team.remove(student);
            
            if (student.getStartupProject() == this) {
                student.setStartupProject(null);
            }
            System.out.println("❌ Студент " + student.getFirstName() + " удален из команды стартапа '" + name + "'.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n🚀 СТАРТАП-ПРОЕКТ: %s [%s]\n", name, status.getDescriptionEng()));
        sb.append(String.format("Описание: %s\n", description));
        sb.append("Состав команды бизнес-инкубатора:\n");
        if (team.isEmpty()) {
            sb.append("  — (Команда еще не сформирована)\n");
        } else {
            for (Student student : team) {
                String major = student.getMajor() != null ? student.getMajor().toString() : "Не указана";
                sb.append(String.format("  • %s (%s) — Специальность: %s\n", 
                        student.getFullName(), student.getEmail(), major));
            }
        }
        return sb.toString();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.requireNonNullElse(name, "Без названия").trim(); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = Objects.requireNonNullElse(description, "").trim(); }

    public List<Student> getTeam() { 
        return Collections.unmodifiableList(team); 
    }

    public StartupStatus getStatus() { return status; }
    public void setStatus(StartupStatus status) { this.status = status != null ? status : StartupStatus.UNDER_REVIEW; }
}