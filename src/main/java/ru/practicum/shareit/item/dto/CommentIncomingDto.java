package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentIncomingDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
