package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item create(Item item, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (item.getAvailable() == null) {
            throw new ValidationException("Доступность предмета должна быть определена");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название предмета не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание предмета не может быть пустым");
        }
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    public ItemInfo findOne(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("id предмета должен быть передан");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));

        List<Booking> bookings = bookingRepository.findByItemIdAndStatusOrderByStartDesc(itemId, BookingStatus.APPROVED);
        List<Comment> comments = commentRepository.findByItemId(itemId);

        return ItemInfo.builder()
                .item(item)
                .bookings(bookings)
                .comments(comments)
                .build();
    }

    @Override
    public List<ItemInfo> findAllByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<Item> items = itemRepository.findByOwnerId(userId);

        List<Long> itemIds = items.stream().map(Item::getId).toList();
        List<Booking> allItemsBookings = bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds, BookingStatus.APPROVED);

        return items.stream()
                .map(item -> ItemInfo.builder()
                        .item(item)
                        .bookings(allItemsBookings)
                        .comments(commentRepository.findByItemId(item.getId()))
                        .build())
                .toList();
    }


    @Override
    @Transactional
    public Comment createComment(Comment comment, Long itemId, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        boolean hasBooked = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now).stream()
                .anyMatch(b -> b.getItem().getId().equals(itemId) && b.getStatus() == BookingStatus.APPROVED);

        if (!hasBooked) {
            throw new ValidationException("Пользователь id " + userId + " не может оставить отзыв: " +
                    "он не арендовал эту вещь или аренда ещё не завершилась");
        }

        comment.setAuthor(author);
        comment.setItem(item);

        return commentRepository.save(comment);
    }

    @Override
    public List<Item> findByText(String text, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text);
    }

    @Override
    @Transactional
    public Item update(Item item, Long itemId, Long userId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));

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
        return itemRepository.save(oldItem);
    }
}
