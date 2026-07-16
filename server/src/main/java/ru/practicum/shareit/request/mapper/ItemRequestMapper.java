package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto dto) {
        if (dto == null) {
            return null;
        }
        ItemRequest request = new ItemRequest();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setCreated(dto.getCreated());
        return request;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        if (request == null) {
            return null;
        }

        List<ItemRequestDto.ItemAnswerDto> answers = Collections.emptyList();
        if (items != null) {
            answers = items.stream()
                    .filter(item -> item.getRequest() != null && item.getRequest().getId().equals(request.getId()))
                    .map(item -> ItemRequestDto.ItemAnswerDto.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                            .build())
                    .toList();
        }

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor() != null ? request.getRequestor().getId() : null)
                .created(request.getCreated())
                .items(answers)
                .build();
    }
}
