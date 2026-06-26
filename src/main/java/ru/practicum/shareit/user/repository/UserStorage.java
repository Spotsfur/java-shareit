package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    List<User> findAll();

    Optional<User> findOne(Long id);

    User update(User user);

    void delete(Long id);
}
