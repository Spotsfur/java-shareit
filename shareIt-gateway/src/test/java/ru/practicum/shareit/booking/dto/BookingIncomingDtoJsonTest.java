package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingIncomingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingIncomingDto> json;

    //Сериализация
    @Test
    void testBookingIncomingDtoSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 12, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 12, 5, 18, 0, 0);

        BookingIncomingDto dto = new BookingIncomingDto();
        dto.setItemId(1L);
        dto.setStart(start);
        dto.setEnd(end);

        JsonContent<BookingIncomingDto> result = json.write(dto);

        //Проверяем id предмета
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);

        //Проверяем даты
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-12-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-12-05T18:00:00");
    }

    //Десериализация
    @Test
    void testBookingIncomingDtoDeserialization() throws Exception {
        String jsonContent = "{\n" +
                "  \"itemId\": 1,\n" +
                "  \"start\": \"2026-12-01T10:00:00\",\n" +
                "  \"end\": \"2026-12-05T18:00:00\"\n" +
                "}";

        BookingIncomingDto result = json.parse(jsonContent).getObject();

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2026, 12, 1, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2026, 12, 5, 18, 0, 0));
    }
}
