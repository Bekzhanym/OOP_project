package kbtu_oop_project.domain.repository;

import kbtu_oop_project.domain.features.user.User;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface UserRepository {
    void add(User user);

    List<User> findAllUsers();

    /** Login lookup: email treated case-insensitively. */
    default Optional<User> findByEmailIgnoreCase(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        String needle = email.trim().toLowerCase(Locale.ROOT);
        return findAllUsers().stream()
                .filter(u -> u.getEmail() != null
                        && needle.equals(u.getEmail().trim().toLowerCase(Locale.ROOT)))
                .findFirst();
    }
}
