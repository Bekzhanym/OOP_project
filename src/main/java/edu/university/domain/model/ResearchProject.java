package edu.university.domain.model;

import java.util.ArrayList;
import java.util.List;

public class ResearchProject {
    private String topic;
    private final List<ResearchPaper> publishedPapers = new ArrayList<>();
    private final List<Researcher> participants = new ArrayList<>();

    public void addParticipant(User user) {
        if (user instanceof Researcher researcher) {
            participants.add(researcher);
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<ResearchPaper> getPublishedPapers() {
        return publishedPapers;
    }

    public List<Researcher> getParticipants() {
        return participants;
    }
}
