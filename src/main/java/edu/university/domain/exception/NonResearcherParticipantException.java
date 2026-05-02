package edu.university.domain.exception;

import edu.university.domain.model.User;

public class NonResearcherParticipantException extends RuntimeException {

    public NonResearcherParticipantException(String message) {
        super(message);
    }

    public NonResearcherParticipantException(User candidate) {
        super(candidate == null
                ? "Cannot add null participant to research project"
                : String.format(
                        "User %s (%s) is not a Researcher and cannot join a ResearchProject",
                        candidate.getId(),
                        candidate.getClass().getSimpleName()));
    }
}
