package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<CommentResponseDto> comments;

    @Data
    @Builder
    public static class ShortBookingDto {
        private Long id;
        private Long bookerId;
    }
}
