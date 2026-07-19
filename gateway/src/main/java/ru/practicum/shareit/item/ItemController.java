package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Создание вещи пользователем id={}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Получение всех вещей пользователя id={}", userId);
        return itemClient.findAllByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findOne(@PathVariable @NotNull(message = "id предмета должен быть передан") Long itemId,
                                          @RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Получение вещи id={} пользователем id={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam String text,
                                             @RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Поиск вещей по тексту '{}' пользователем id={}", text, userId);
        return itemClient.searchItems(text, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemUpdateDto itemDto,
                                         @PathVariable @NotNull(message = "id предмета должен быть передан") Long itemId,
                                         @RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Обновление вещи id={} пользователем id={}", itemId, userId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable @NotNull(message = "id предмета должен быть передан") Long itemId,
                                                @RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId,
                                                @Valid @RequestBody CommentIncomingDto incomingDto) {
        log.info("Gateway: Добавление комментария к вещи id={} пользователем id={}", itemId, userId);
        return itemClient.createComment(itemId, userId, incomingDto);
    }
}
