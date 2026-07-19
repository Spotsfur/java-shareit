package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Название предмета не может быть пустым")
    private String name;
    @NotBlank(message = "Описание предмета не может быть пустым")
    private String description;
    @NotNull(message = "Доступность предмета должна быть определена")
    private Boolean available;
    //Постман валится без этой аннотации
    @JsonProperty("requestId")
    private Long request;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<CommentResponseDto> comments;

    @Data
    @Builder
    public static class ShortBookingDto {
        private Long id;
        private Long bookerId;
        private java.time.LocalDateTime start;
        private java.time.LocalDateTime end;
    }
}
