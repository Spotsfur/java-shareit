package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Владелец Вещи");
        owner.setEmail("owner-booking@mail.ru");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Арендатор Вещи");
        booker.setEmail("booker-booking@mail.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Ударная");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_whenValid_shouldSaveBookingWithWaitingStatus() {
        Booking saved = bookingService.create(booking, item.getId(), booker.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(saved.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(saved.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void create_whenOwnerTriesToBookOwnItem_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.create(booking, item.getId(), owner.getId()));
    }

    @Test
    void create_whenItemNotAvailable_shouldThrowValidationException() {
        item.setAvailable(false);
        itemRepository.save(item);

        assertThrows(ValidationException.class, () ->
                bookingService.create(booking, item.getId(), booker.getId()));
    }

    @Test
    void approve_whenApprovedTrue_shouldChangeStatusToApproved() {
        Booking saved = bookingService.create(booking, item.getId(), booker.getId());

        Booking approved = bookingService.approve(saved.getId(), owner.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approve_whenUserIsNotOwner_shouldThrowValidationException() {
        Booking saved = bookingService.create(booking, item.getId(), booker.getId());

        assertThrows(ValidationException.class, () ->
                bookingService.approve(saved.getId(), booker.getId(), true));
    }

    @Test
    void findOne_whenRequestedByBookerOrOwner_shouldReturnBooking() {
        Booking saved = bookingService.create(booking, item.getId(), booker.getId());

        Booking foundByBooker = bookingService.findOne(saved.getId(), booker.getId());
        Booking foundByOwner = bookingService.findOne(saved.getId(), owner.getId());

        assertThat(foundByBooker).isNotNull();
        assertThat(foundByOwner).isNotNull();
    }

    @Test
    void findOne_whenRequestedByStranger_shouldThrowNotFoundException() {
        Booking saved = bookingService.create(booking, item.getId(), booker.getId());

        User stranger = new User();
        stranger.setName("Чужак");
        stranger.setEmail("stranger@mail.ru");
        stranger = userRepository.save(stranger);

        User finalStranger = stranger;
        assertThrows(NotFoundException.class, () ->
                bookingService.findOne(saved.getId(), finalStranger.getId()));
    }

    @Test
    void findAllByBooker_shouldReturnCorrectBookingsList() {
        bookingService.create(booking, item.getId(), booker.getId());

        List<Booking> allBookings = bookingService.findAllByBooker(booker.getId(), "ALL");
        List<Booking> futureBookings = bookingService.findAllByBooker(booker.getId(), "FUTURE");
        List<Booking> waitingBookings = bookingService.findAllByBooker(booker.getId(), "WAITING");

        assertThat(allBookings).hasSize(1);
        assertThat(futureBookings).hasSize(1);
        assertThat(waitingBookings).hasSize(1);
    }

    @Test
    void findAllByOwner_shouldReturnCorrectBookingsList() {
        bookingService.create(booking, item.getId(), booker.getId());

        List<Booking> allBookings = bookingService.findAllByOwner(owner.getId(), "ALL");
        assertThat(allBookings).hasSize(1);
    }
}
