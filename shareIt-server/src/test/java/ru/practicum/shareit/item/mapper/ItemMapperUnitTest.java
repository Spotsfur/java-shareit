package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperUnitTest {

    @Test
    void testNullInputs() {
        assertThat(ItemMapper.toItemDto((Item) null)).isNull();
        assertThat(ItemMapper.toItem((ItemDto) null)).isNull();
        assertThat(ItemMapper.toItemDto((ItemInfo) null, 1L)).isNull();
    }

    @Test
    void testToItemWithRequest() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Ударная")
                .available(true)
                .request(100L)
                .build();

        Item item = ItemMapper.toItem(dto);
        assertThat(item.getRequest()).isNotNull();
        assertThat(item.getRequest().getId()).isEqualTo(100L);
    }

    @Test
    void testToItemDtoWithRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(200L);

        Item item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Ударная")
                .available(true)
                .request(request)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);
        assertThat(dto.getRequest()).isEqualTo(200L);
    }

    @Test
    void testToItemDtoWithNullCommentsAndActiveBookingsForOwner() {
        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = Item.builder()
                .id(10L)
                .name("Молоток")
                .description("Слесарный")
                .available(true)
                .owner(owner)
                .build();

        Booking last = new Booking();
        last.setId(100L);
        last.setBooker(booker);
        last.setStart(LocalDateTime.now().minusDays(2));
        last.setEnd(LocalDateTime.now().minusDays(1));
        last.setStatus(BookingStatus.APPROVED);

        Booking next = new Booking();
        next.setId(101L);
        next.setBooker(booker);
        next.setStart(LocalDateTime.now().plusDays(1));
        next.setEnd(LocalDateTime.now().plusDays(2));
        next.setStatus(BookingStatus.APPROVED);

        ItemInfo info = ItemInfo.builder()
                .item(item)
                .lastBooking(last)
                .nextBooking(next)
                .comments(null)
                .build();

        ItemDto dto = ItemMapper.toItemDto(info, 1L);

        assertThat(dto.getComments()).isEmpty();
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId()).isEqualTo(100L);
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getId()).isEqualTo(101L);
    }
}
