package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Override
    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email пользователя");
        }
        boolean emailExists = storage.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        if (emailExists) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        return storage.create(user);
    }

    @Override
    public User findOne(Long id) {
        if (id == null) {
            throw new ValidationException("id пользователя должен быть передан");
        }
        Optional<User> optionalUser = storage.findOne(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return optionalUser.get();
    }

    @Override
    public User update(User user, Long id) {
        if (id == null) {
            throw new ValidationException("id пользователя должен быть передан");
        }
        Optional<User> optionalUser = storage.findOne(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        if (user.getEmail() != null) {
            if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                throw new ValidationException("Некорректный email пользователя");
            }
            boolean emailExists = storage.findAll().stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
            if (emailExists) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        } else {
            user.setEmail(optionalUser.get().getEmail());
        }

        if (user.getName() == null) {
            user.setName(optionalUser.get().getName());
        }
        user.setId(id);
        return storage.update(user);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new ValidationException("id пользователя должен быть передан");
        }
        Optional<User> optionalUser = storage.findOne(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        storage.delete(id);
    }
}