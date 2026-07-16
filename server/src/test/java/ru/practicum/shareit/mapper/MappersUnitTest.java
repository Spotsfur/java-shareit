package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MappersUnitTest {

    @Test
    void testUserMapper() {
        UserDto dto = UserDto.builder().id(1L).name("Ivan").email("i@mail.ru").build();
        User user = UserMapper.toUser(dto);

        assertThat(user.getName()).isEqualTo("Ivan");

        UserDto backDto = UserMapper.toUserDto(user);
        assertThat(backDto.getName()).isEqualTo("Ivan");
    }

    @Test
    void testItemMapper() {
        ItemDto dto = ItemDto.builder().id(1L).name("Отвертка").description("Обычная").available(true).build();
        Item item = ItemMapper.toItem(dto);

        assertThat(item.getName()).isEqualTo("Отвертка");

        ItemDto backDto = ItemMapper.toItemDto(item);
        assertThat(backDto.getName()).isEqualTo("Отвертка");

        ItemInfo info = ItemInfo.builder()
                .item(item)
                .comments(List.of())
                .build();

        ItemDto infoDto = ItemMapper.toItemDto(info, 1L);
        assertThat(infoDto.getName()).isEqualTo("Отвертка");
    }
}
