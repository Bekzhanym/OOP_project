package kbtu_oop_project.domain.exception;

import kbtu_oop_project.domain.features.user.User;

public class SupervisorQualificationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final int MIN_SUPERVISOR_H_INDEX = 3;

    public SupervisorQualificationException(String message) {
        super(message);
    }

    public static SupervisorQualificationException belowMinimum(User supervisor, int actualHIndex) {
        if (supervisor == null) {
            return new SupervisorQualificationException(String.format(
                    "Ошибка назначения руководителя: Объект руководителя не инициализирован (null). " +
                    "Требуемый минимальный h-index >= %d", MIN_SUPERVISOR_H_INDEX));
        }

        return new SupervisorQualificationException(String.format(
                "Отказ в назначении руководителя: %s %s (ID: %s, Роль: %s). " +
                "Научный руководитель дипломных проектов (4 курс) должен иметь h-index >= %d. (Фактический индекс: %d)",
                supervisor.getFirstName(),
                supervisor.getLastName(),
                supervisor.getId(),
                supervisor.getClass().getSimpleName(), 
                MIN_SUPERVISOR_H_INDEX,
                actualHIndex));
    }

    public static int minimumRequired() {
        return MIN_SUPERVISOR_H_INDEX;
    }
}