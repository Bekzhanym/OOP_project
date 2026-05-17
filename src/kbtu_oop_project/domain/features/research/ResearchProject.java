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
    
    private final List<User> participants = new ArrayList<>();

    public ResearchProject() {
    }

    public ResearchProject(String topic) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Тема исследовательского проекта не может быть пустой");
        }
        this.topic = topic.trim();
    }

    public ResearchProject(String topic, User leader) {
        this(topic);
        if (leader != null) {
            if (!isUserResearcher(leader)) {
                throw new NotAResearcherException("Лидер проекта должен иметь статус исследователя!");
            }
            this.participants.add(leader);
            this.leaderName = leader.getFullName();
        }
    }

    private boolean isUserResearcher(User user) {
        return user instanceof Researcher;
    }

    public void addParticipant(User user) {
        if (user == null) return;
        
        if (!isUserResearcher(user)) {
            throw new NonResearcherParticipantException(user);
        }
        
        if (!participants.contains(user)) {
            participants.add(user);
        }
    }

    public void joinProject(User user) {
        if (user == null) return;
        
        if (!isUserResearcher(user)) {
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
        sb.append("Руководитель: ").append(getLeaderName()).append("\n");
        sb.append("========================================\n");

        sb.append("Участники проекта:\n");
        if (participants.isEmpty()) {
            sb.append("  — Нет участников\n");
        } else {
            for (User u : participants) {
                sb.append(String.format("  • %s (%s) [Роль: %s | h-index: %d]\n",
                        u.getFullName(), u.getEmail(), u.getUserRole(),
                        (u instanceof Researcher r ? r.getHIndex() : 0)));
            }
        }

        sb.append("\nОпубликованные труды проекта:\n");
        if (publishedPapers.isEmpty()) {
            sb.append("  — Нет публикаций по данному проекту\n");
        } else {
            for (ResearchPaper paper : publishedPapers) {
                sb.append("  • ").append(paper.getTitle())
                  .append(paper.getDoi().isEmpty() ? "" : " (DOI: " + paper.getDoi() + ")")
                  .append("\n");
            }
        }
        sb.append("----------------------------------------");
        return sb.toString();
    }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getLeaderName() { return leaderName != null ? leaderName : "—"; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }

    public List<ResearchPaper> getPublishedPapers() { 
        return Collections.unmodifiableList(publishedPapers); 
    }
    
    public List<User> getParticipants() { 
        return Collections.unmodifiableList(participants); 
    }
}