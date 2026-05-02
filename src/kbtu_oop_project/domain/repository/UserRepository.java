package kbtu_oop_project.domain.repository;

import kbtu_oop_project.domain.features.user.User;

import java.util.List;

public interface UserRepository {
    void add(User user);

    List<User> findAllUsers();
}
