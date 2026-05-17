package kbtu_oop_project.domain.exception;

import kbtu_oop_project.domain.features.user.User;

public class NonResearcherParticipantException extends RuntimeException {

    public NonResearcherParticipantException(String message) {
        super(message);
    }

    public NonResearcherParticipantException(User candidate) {
        super(candidate == null
                ? "Cannot add null participant to research project"
                : String.format(
                        "User [ID: %s, Role: %s] is not a Researcher and cannot join a ResearchProject",
                        candidate.getId(),
                        candidate.getClass().getSimpleName()));
    }

    public NonResearcherParticipantException(String message, Throwable cause) {
        super(message, cause);
    }
}