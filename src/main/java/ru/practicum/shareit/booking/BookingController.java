package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @Valid @RequestBody BookingIncomingDto incomingDto) {
        log.info("Пользователь id={} запрашивает бронирование", userId);
        Booking booking = BookingMapper.toBooking(incomingDto);
        Booking savedBooking = bookingService.create(booking, incomingDto.getItemId(), userId);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        log.info("Изменение статуса бронирования id={} пользователем id={}", bookingId, userId);
        Booking updatedBooking = bookingService.approve(bookingId, userId, approved);
        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findOne(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @PathVariable Long bookingId) {
        log.info("Запрос бронирования id={} пользователем id={}", bookingId, userId);
        return BookingMapper.toBookingResponseDto(bookingService.findOne(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> findAllByBooker(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос бронирований букера id={} со статусом state={}", userId, state);
        return bookingService.findAllByBooker(userId, state).stream()
                .map(BookingMapper::toBookingResponseDto)
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос бронирований для вещей владельца id={} со статусом state={}", userId, state);
        return bookingService.findAllByOwner(userId, state).stream()
                .map(BookingMapper::toBookingResponseDto)
                .toList();
    }
}
