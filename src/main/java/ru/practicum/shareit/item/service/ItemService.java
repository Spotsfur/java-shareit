package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;

import java.util.List;

public interface ItemService {

    Item create(Item item, Long userId);

    List<ItemInfo> findAllByUserId(Long userId);

    List<Item> findByText(String text, Long userId);

    ItemInfo findOne(Long itemId);

    Item update(Item item, Long itemId, Long userId);

    Comment createComment(Comment comment, Long itemId, Long userId);
}
