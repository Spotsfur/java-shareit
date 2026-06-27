package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
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
        ItemDto dto = toItemDto(item);

        if (info.getComments() != null) {
            dto.setComments(info.getComments().stream()
                    .map(CommentMapper::toCommentResponseDto)
                    .toList());
        } else {
            dto.setComments(List.of());
        }

        if (item.getOwner() != null && item.getOwner().getId().equals(userId) && info.getBookings() != null) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();

            info.getBookings().stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()))
                    .filter(b -> b.getStatus() == ru.practicum.shareit.booking.enums.BookingStatus.APPROVED)
                    .filter(b -> b.getStart().isBefore(now))
                    .max(java.util.Comparator.comparing(Booking::getEnd))
                    .ifPresent(last -> dto.setLastBooking(ItemDto.ShortBookingDto.builder()
                            .id(last.getId())
                            .bookerId(last.getBooker().getId())
                            .start(last.getStart())
                            .end(last.getEnd())
                            .build()));

            info.getBookings().stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()))
                    .filter(b -> b.getStatus() == ru.practicum.shareit.booking.enums.BookingStatus.APPROVED)
                    .filter(b -> b.getStart().isAfter(now))
                    .min(java.util.Comparator.comparing(Booking::getStart))
                    .ifPresent(next -> dto.setNextBooking(ItemDto.ShortBookingDto.builder()
                            .id(next.getId())
                            .bookerId(next.getBooker().getId())
                            .start(next.getStart())
                            .end(next.getEnd())
                            .build()));
        }
        return dto;
    }
}
