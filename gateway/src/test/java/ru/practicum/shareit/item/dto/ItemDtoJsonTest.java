package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    //Сериализация
    @Test
    void testItemDtoSerialization() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Ударная дрель")
                .available(true)
                .request(10L)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Ударная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(10);
        assertThat(result).doesNotHaveJsonPath("$.request");
    }

    //Десериализация
    @Test
    void testItemDtoDeserialization() throws Exception {
        String jsonContent = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Дрель\",\n" +
                "  \"description\": \"Ударная дрель\",\n" +
                "  \"available\": true,\n" +
                "  \"requestId\": 10\n" +
                "}";

        ItemDto result = json.parse(jsonContent).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Ударная дрель");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequest()).isEqualTo(10L);
    }
}
