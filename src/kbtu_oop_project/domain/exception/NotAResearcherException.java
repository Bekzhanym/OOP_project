package kbtu_oop_project.domain.exception;

public class NotAResearcherException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotAResearcherException(String message) {
        super(message);
    }
}
