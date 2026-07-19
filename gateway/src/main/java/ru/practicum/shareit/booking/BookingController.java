package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId,
                                         @Valid @RequestBody BookingIncomingDto incomingDto) {
        log.info("Gateway: Пользователь id={} запрашивает бронирование", userId);
        return bookingClient.bookItem(userId, incomingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId,
                                          @PathVariable @NotNull(message = "id бронирования должно быть передано") Long bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Gateway: Изменение статуса бронирования id={} пользователем id={}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findOne(@RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId,
                                          @PathVariable @NotNull(message = "id бронирования должно быть передано") Long bookingId) {
        log.info("Gateway: Запрос бронирования id={} пользователем id={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(
            @RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.info("Gateway: Запрос бронирований букера id={} со статусом state={}", userId, state);
        return bookingClient.getBookingsByBooker(userId, state.name());
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(
            @RequestHeader(USER_ID_HEADER) @NotNull(message = "id пользователя должен быть передан") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        log.info("Gateway: Запрос бронирований для вещей владельца id={} со статусом state={}", userId, state);
        return bookingClient.getBookingsByOwner(userId, state.name());
    }
}
