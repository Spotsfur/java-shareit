package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingIncomingDto {
    @NotNull(message = "ID предмета не может быть пустым")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования не может быть пустой")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    private LocalDateTime end;

    @AssertTrue(message = "Время окончания бронирования должно быть позже времени начала, и даты не должны быть в прошлом")
    public boolean isBookingDatesValid() {
        if (start == null || end == null) {
            return false;
        }

        //Попытка починить постман
        if (start.isBefore(LocalDateTime.now().minusMinutes(1))) {
            return false;
        }
        return start.isBefore(end);
    }
}
