package kbtu_oop_project.domain.exception;

import kbtu_oop_project.domain.features.user.User;

public class NotAResearcherException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotAResearcherException(String message) {
        super(message);
    }

    public NotAResearcherException(User user) {
        super(user == null 
                ? "Действие доступно только для исследователей (объект пользователя равен null)."
                : String.format(
                        "Критическая ошибка доступа: Пользователь [ID: %s, Текущая роль: %s] " +
                        "не обладает правами исследователя для выполнения этой научной операции.",
                        user.getId(),
                        user.getClass().getSimpleName())); 
    }
}