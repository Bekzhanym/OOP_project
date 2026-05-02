package kbtu_oop_project.domain.features.research;

import kbtu_oop_project.domain.exception.NonResearcherParticipantException;
import kbtu_oop_project.domain.features.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResearchProject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String topic;
    private final List<ResearchPaper> publishedPapers = new ArrayList<>();
    private final List<Researcher> participants = new ArrayList<>();

    public void addParticipant(User user) {
        if (!(user instanceof Researcher researcher)) {
            throw new NonResearcherParticipantException(user);
        }
        participants.add(researcher);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<ResearchPaper> getPublishedPapers() {
        return Collections.unmodifiableList(publishedPapers);
    }

    public List<Researcher> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    /** Adds paper linked to this project (storage helper). */
    public void addPublishedPaper(ResearchPaper paper) {
        if (paper != null) {
            publishedPapers.add(paper);
        }
    }
}
