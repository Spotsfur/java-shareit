package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemAnswerDto> items;

    @Data
    @Builder
    public static class ItemAnswerDto {
        private Long id;
        private String name;
        private Long ownerId;
    }
}
