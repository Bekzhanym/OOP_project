package edu.university.domain.repository;

import edu.university.domain.model.User;

import java.util.List;

public interface UserRepository {
    void add(User user);

    List<User> findAllUsers();
}
