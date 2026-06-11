package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create (Item item, Long userId);

    List<Item> findAllByUserId(Long userId);

    List<Item> findByText(String text, Long userId);

    Item findOne(Long itemId);

    Item update(Item item, Long itemId, Long userId);

}
