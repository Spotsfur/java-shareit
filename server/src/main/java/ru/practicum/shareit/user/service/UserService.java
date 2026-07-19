package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public interface UserService {

    User create(User user);

    User findOne(Long id);

    User update(User user, Long id);

    void delete(Long id);
}
