package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Некорректное имя пользователя");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email пользователя");
        }
        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }

    @Override
    public User findOne(Long id) {
        if (id == null) {
            throw new ValidationException("id пользователя должен быть передан");
        }
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    @Transactional
    public User update(User user, Long id) {
        if (id == null) {
            throw new ValidationException("id пользователя должен быть передан");
        }

        User existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (user.getEmail() != null) {
            if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                throw new ValidationException("Некорректный email пользователя");
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            if (user.getName().isBlank()) {
                throw new ValidationException("Некорректное имя пользователя");
            }
            existingUser.setName(user.getName());
        }

        try {
            return repository.save(existingUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new ValidationException("id пользователя должен быть передан");
        }
        if (!repository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        repository.deleteById(id);
    }
}