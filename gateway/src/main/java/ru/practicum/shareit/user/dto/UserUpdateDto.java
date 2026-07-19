package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {
    private Long id;

    @Size(min = 1, message = "Некорректное имя пользователя")
    private String name;

    @Email(message = "Некорректный формат email")
    private String email;
}
