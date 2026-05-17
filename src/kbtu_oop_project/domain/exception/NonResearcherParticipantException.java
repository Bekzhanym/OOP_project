package kbtu_oop_project.domain.exception;

import kbtu_oop_project.domain.features.user.User;

public class NonResearcherParticipantException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NonResearcherParticipantException(String message) {
        super(message);
    }

    public NonResearcherParticipantException(User candidate) {
        super(candidate == null
                ? "Cannot add null participant to research project"
                : String.format(
                        "Пользователь [ID: %s, Настоящая Роль: %s] не имеет подтвержденного статуса Исследователя и не может быть добавлен в ResearchProject.",
                        candidate.getId(),
                        candidate.getClass().getSimpleName())); 
    }

    public NonResearcherParticipantException(String message, Throwable cause) {
        super(message, cause);
    }
}