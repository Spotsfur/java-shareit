package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping()
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        Item item = service.create(ItemMapper.toItem(itemDto), userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping()
    public List<ItemDto> findAllByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        List<Item> items = service.findAllByUserId(userId);
        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text,
                                @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        List<Item> items = service.findByText(text, userId);
        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    @GetMapping("/{itemId}")
    public ItemDto findOne(@PathVariable Long itemId) {
        Item item = service.findOne(itemId);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        Item item = service.update(ItemMapper.toItem(itemDto), itemId, userId);
        return ItemMapper.toItemDto(item);
    }
}
