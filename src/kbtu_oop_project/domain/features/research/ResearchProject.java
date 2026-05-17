package kbtu_oop_project.domain.features.research;

import kbtu_oop_project.domain.exception.NonResearcherParticipantException;
import kbtu_oop_project.domain.exception.NotAResearcherException;
import kbtu_oop_project.domain.features.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResearchProject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String topic;
    private String leaderName;
    private final List<ResearchPaper> publishedPapers = new ArrayList<>();
    private final List<Researcher> participants = new ArrayList<>();

    public ResearchProject() {
    }

    public ResearchProject(String topic) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Тема исследовательского проекта не может быть пустой");
        }
        this.topic = topic.trim();
    }

    public ResearchProject(String topic, Researcher leader) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Тема исследовательского проекта не может быть пустой");
        }
        this.topic = topic.trim();
        if (leader != null) {
            participants.add(leader);
            if (leader instanceof User u) {
                this.leaderName = u.getFullName();
            }
        }
    }

    public void addParticipant(User user) {
        if (!(user instanceof Researcher researcher)) {
            throw new NonResearcherParticipantException(user);
        }
        if (!participants.contains(researcher)) {
            participants.add(researcher);
        }
    }

    public void joinProject(User user) {
        if (!(user instanceof Researcher)) {
            throw new NotAResearcherException("User " + user.getEmail() + " is not a Researcher.");
        }
        addParticipant(user);
    }

    public void addPublishedPaper(ResearchPaper paper) {
        if (paper != null && !publishedPapers.contains(paper)) {
            publishedPapers.add(paper);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("НАУЧНЫЙ ПРОЕКТ: ").append(topic).append("\n");
        sb.append("========================================\n");

        sb.append("Участники (Researchers):\n");
        if (participants.isEmpty()) {
            sb.append("  — Нет участников\n");
        } else {
            for (Researcher r : participants) {
                if (r instanceof User u) {
                    sb.append(String.format("  • %s (%s) [h-index: %d]\n", u.getFullName(), u.getEmail(), r.getHIndex()));
                } else {
                    sb.append(String.format("  • Исследователь [h-index: %d]\n", r.getHIndex()));
                }
            }
        }

        sb.append("\nОпубликованные труды проекта:\n");
        if (publishedPapers.isEmpty()) {
            sb.append("  — Нет публикаций по данному проекту\n");
        } else {
            for (ResearchPaper paper : publishedPapers) {
                sb.append("  • ").append(paper.getTitle()).append(" (DOI: ").append(paper.getDoi()).append(")\n");
            }
        }
        sb.append("----------------------------------------");
        return sb.toString();
    }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getLeaderName() { return leaderName != null ? leaderName : "—"; }

    public List<ResearchPaper> getPublishedPapers() { return Collections.unmodifiableList(publishedPapers); }
    public List<Researcher> getParticipants() { return Collections.unmodifiableList(participants); }
}
