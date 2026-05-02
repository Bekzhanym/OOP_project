package kbtu_oop_project.domain.exception;

public class SupervisorQualificationException extends RuntimeException {

    private static final int MIN_SUPERVISOR_H_INDEX = 3;

    public SupervisorQualificationException(String message) {
        super(message);
    }

    public static SupervisorQualificationException belowMinimum(int actualHIndex) {
        return new SupervisorQualificationException(String.format(
                "4th-year research supervisor must have h-index >= %d (given: %d)",
                MIN_SUPERVISOR_H_INDEX,
                actualHIndex));
    }

    public static int minimumRequired() {
        return MIN_SUPERVISOR_H_INDEX;
    }
}
