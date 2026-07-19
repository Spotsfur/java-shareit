package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
@Builder
public class ItemUpdateDto {
    @Size(min = 1, message = "Название предмета не может быть пустым")
    private String name;

    @Size(min = 1, message = "Описание предмета не может быть пустым")
    private String description;

    private Boolean available;
}
