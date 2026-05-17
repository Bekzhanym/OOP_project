package kbtu_oop_project.domain.exception;

import kbtu_oop_project.domain.features.user.User; 

public class SupervisorQualificationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final int MIN_SUPERVISOR_H_INDEX = 3;

    public SupervisorQualificationException(String message) {
        super(message);
    }

    public static SupervisorQualificationException belowMinimum(User supervisor, int actualHIndex) {
        return new SupervisorQualificationException(String.format(
                "Cannot assign supervisor %s %s (ID: %s). 4th-year research supervisor must have h-index >= %d (given: %d)",
                supervisor.getFirstName(),
                supervisor.getLastName(),
                supervisor.getId(),
                MIN_SUPERVISOR_H_INDEX,
                actualHIndex));
    }

    public static int minimumRequired() {
        return MIN_SUPERVISOR_H_INDEX;
    }
}