package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @Valid @RequestBody BookingIncomingDto incomingDto) {
        log.info("Gateway: Пользователь id={} запрашивает бронирование", userId);
        return bookingClient.bookItem(userId, incomingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Gateway: Изменение статуса бронирования id={} пользователем id={}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findOne(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable Long bookingId) {
        log.info("Gateway: Запрос бронирования id={} пользователем id={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(@RequestHeader(USER_ID_HEADER) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("Gateway: Запрос бронирований букера id={} со статусом state={}", userId, stateParam);
        BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        return bookingClient.getBookingsByBooker(userId, stateParam);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("Gateway: Запрос бронирований для вещей владельца id={} со статусом state={}", userId, stateParam);
        BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        return bookingClient.getBookingsByOwner(userId, stateParam);
    }
}
