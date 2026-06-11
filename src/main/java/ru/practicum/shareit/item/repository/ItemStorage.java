package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    List<Item> findAll();

    Optional<Item> findOne(Long itemId);

    Item update(Item item);
}
