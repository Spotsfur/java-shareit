package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto dto) {
        if (dto == null) {
            return null;
        }
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    //Из-за того, что на этапе проектирования я решил что маппить DTO и обратно я буду в контроллере,
    //у меня возникла сложность, при которой я должен был донести полные данные до контроллера.
    //Но мой сервис не знает про DTO, потому работаю в промежуточном ItemInfo
    public static ItemDto toItemDto(ItemInfo info, Long userId) {
        if (info == null) {
            return null;
        }

        Item item = info.getItem();
        ItemDto dto = toItemDto(item); // Мапим базовые поля

        if (info.getComments() != null) {
            dto.setComments(info.getComments().stream()
                    .map(CommentMapper::toCommentResponseDto)
                    .toList());
        } else {
            dto.setComments(List.of());
        }

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {

            if (info.getLastBooking() != null) {
                dto.setLastBooking(ItemDto.ShortBookingDto.builder()
                        .id(info.getLastBooking().getId())
                        .bookerId(info.getLastBooking().getBooker().getId())
                        .start(info.getLastBooking().getStart()) // Даты для ревьюера!
                        .end(info.getLastBooking().getEnd())
                        .build());
            }

            if (info.getNextBooking() != null) {
                dto.setNextBooking(ItemDto.ShortBookingDto.builder()
                        .id(info.getNextBooking().getId())
                        .bookerId(info.getNextBooking().getBooker().getId())
                        .start(info.getNextBooking().getStart()) // Даты для ревьюера!
                        .end(info.getNextBooking().getEnd())
                        .build());
            }
        }
        return dto;
    }

}
