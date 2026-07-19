package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

public interface BookingService {
    Booking create(Booking booking, Long itemId, Long userId);

    Booking approve(Long bookingId, Long userId, Boolean approved);

    Booking findOne(Long bookingId, Long userId);

    List<Booking> findAllByBooker(Long userId, String state);

    List<Booking> findAllByOwner(Long userId, String state);
}
