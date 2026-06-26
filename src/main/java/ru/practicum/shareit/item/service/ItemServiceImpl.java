package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage storage;
    private final UserStorage userStorage;

    @Override
    public Item create(Item item, Long userId) {
        Optional<User> optionalUser = userStorage.findOne(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Доступность предмета должна быть определена");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название предмета не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание предмета не может быть пустым");
        }
        item.setOwner(optionalUser.get());
        return storage.create(item);
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        Optional<User> optionalUser = userStorage.findOne(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return storage.findAll().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> findByText(String text, Long userId) {
        Optional<User> optionalUser = userStorage.findOne(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        //Не лезем в базу если строка пустая
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String lowerCaseText = text.toLowerCase();
        return storage.findAll().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item -> {
                    String name = item.getName() != null ? item.getName().toLowerCase() : "";
                    String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    return name.contains(lowerCaseText) || description.contains(lowerCaseText);
                })
                .toList();
    }

    @Override
    public Item findOne(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("id предмета должен быть передан");
        }
        Optional<Item> optionalItem = storage.findOne(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        return optionalItem.get();
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) {
        Optional<Item> optionalItem = storage.findOne(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        Item oldItem = optionalItem.get();

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не является владельцем этого предмета");
        }
        if (item.getName() != null) {
            if (item.getName().isBlank()) {
                throw new ValidationException("Название предмета не может быть пустым");
            }
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            if (item.getDescription().isBlank()) {
                throw new ValidationException("Описание предмета не может быть пустым");
            }
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return storage.update(oldItem);
    }

}
