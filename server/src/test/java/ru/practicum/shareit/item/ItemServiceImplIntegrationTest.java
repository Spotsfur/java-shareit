package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@mail.ru");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Арендатор");
        booker.setEmail("booker@mail.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Ударная дрель Makita");
        item.setAvailable(true);
    }

    @Test
    void create_whenItemIsValid_shouldSaveToDatabase() {
        Item savedItem = itemService.create(item, owner.getId());

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void update_whenUserIsOwner_shouldUpdateFields() {
        Item savedItem = itemService.create(item, owner.getId());

        Item updateData = new Item();
        updateData.setName("Новое название");
        updateData.setDescription("Новое описание");

        Item updatedItem = itemService.update(updateData, savedItem.getId(), owner.getId());

        assertThat(updatedItem.getName()).isEqualTo("Новое название");
        assertThat(updatedItem.getDescription()).isEqualTo("Новое описание");
    }

    @Test
    void update_whenUserIsNotOwner_shouldThrowNotFoundException() {
        Item savedItem = itemService.create(item, owner.getId());
        Item updateData = new Item();
        updateData.setName("Взлом");

        assertThrows(NotFoundException.class, () ->
                itemService.update(updateData, savedItem.getId(), booker.getId()));
    }

    @Test
    void findOne_shouldReturnItemInfoWithBookingsAndComments() {
        Item savedItem = itemService.create(item, owner.getId());

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setItem(savedItem);
        pastBooking.setBooker(booker);
        bookingRepository.save(pastBooking);

        Comment comment = new Comment();
        comment.setText("Инструмент супер!");
        comment.setCreated(LocalDateTime.now());
        itemService.createComment(comment, savedItem.getId(), booker.getId());

        ItemInfo info = itemService.findOne(savedItem.getId());

        assertThat(info).isNotNull();
        assertThat(info.getItem().getId()).isEqualTo(savedItem.getId());
        assertThat(info.getComments()).hasSize(1);
        assertThat(info.getComments().get(0).getText()).isEqualTo("Инструмент супер!");
    }

    @Test
    void findAllByUserId_shouldReturnAllUserItems() {
        itemService.create(item, owner.getId());

        Item secondItem = new Item();
        secondItem.setName("Перфоратор");
        secondItem.setDescription("Мощный перфоратор");
        secondItem.setAvailable(true);
        itemService.create(secondItem, owner.getId());

        List<ItemInfo> ownerItems = itemService.findAllByUserId(owner.getId());

        assertThat(ownerItems).hasSize(2);
    }


    @Test
    void createComment_whenUserDidNotBook_shouldThrowValidationException() {
        Item savedItem = itemService.create(item, owner.getId());
        Comment comment = new Comment();
        comment.setText("Я мимо проходил");

        assertThrows(ValidationException.class, () ->
                itemService.createComment(comment, savedItem.getId(), booker.getId()));
    }
}
