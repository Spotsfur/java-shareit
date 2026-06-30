package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

//Так как я делал маппинг в контроллерах, приходится из сервиса передавать этот промежуточный компонент
@Data
@Builder
public class ItemInfo {
    private Item item;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;
}
