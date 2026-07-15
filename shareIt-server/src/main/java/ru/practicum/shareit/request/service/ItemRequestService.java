package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Map;

public interface ItemRequestService {
    ItemRequest create(ItemRequest request, Long userId);

    List<ItemRequest> findAllByUserId(Long userId);

    List<ItemRequest> findAllOfOthers(Long userId);

    ItemRequest findOne(Long requestId, Long userId);

    List<Item> findItemsForRequests(List<ItemRequest> requests);
}
