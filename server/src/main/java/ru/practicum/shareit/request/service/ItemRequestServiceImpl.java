package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest create(ItemRequest request, Long userId) {
        User requestor = checkUserExists(userId);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Override
    public List<ItemRequest> findAllByUserId(Long userId) {
        checkUserExists(userId);
        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
    }

    @Override
    public List<ItemRequest> findAllOfOthers(Long userId) {
        checkUserExists(userId);
        return requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
    }

    @Override
    public ItemRequest findOne(Long requestId, Long userId) {
        checkUserExists(userId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
    }

    @Override
    public List<Item> findItemsForRequests(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        return itemRepository.findAllByRequestIdIn(requestIds);
    }

    private User checkUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
