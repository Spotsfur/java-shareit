package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookedItemDto item;
    private BookerDto booker;

    @Data
    @Builder
    public static class BookedItemDto {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class BookerDto {
        private Long id;
        private String name;
    }
}
