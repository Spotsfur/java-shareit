package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Test
    void create_whenUserNotFound_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.create(new Item(), 1L));
    }

    @Test
    void create_whenAvailableIsNull_shouldThrowValidationException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Item item = new Item();
        assertThrows(ValidationException.class, () -> itemService.create(item, 1L));
    }

    @Test
    void create_whenNameIsEmpty_shouldThrowValidationException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Item item = new Item();
        item.setAvailable(true);
        item.setName("");
        assertThrows(ValidationException.class, () -> itemService.create(item, 1L));
    }

    @Test
    void create_whenDescriptionIsEmpty_shouldThrowValidationException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Item item = new Item();
        item.setAvailable(true);
        item.setName("Дрель");
        item.setDescription("  ");
        assertThrows(ValidationException.class, () -> itemService.create(item, 1L));
    }

    @Test
    void create_whenRequestNotFound_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        ItemRequest fakeRequest = new ItemRequest();
        fakeRequest.setId(100L);
        Item item = new Item();
        item.setAvailable(true);
        item.setName("Дрель");
        item.setDescription("Ударная");
        item.setRequest(fakeRequest);

        Mockito.when(requestRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(item, 1L));
    }

    @Test
    void findOne_whenIdIsNull_shouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> itemService.findOne(null));
    }

    @Test
    void findByText_whenTextIsBlank_shouldReturnEmptyList() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        List<Item> result = itemService.findByText("   ", 1L);

        assertThat(result).isEmpty();
    }

    @Test
    void update_whenUserIsNotOwner_shouldThrowNotFoundException() {
        User owner = new User();
        owner.setId(1L);
        Item existingItem = new Item();
        existingItem.setId(10L);
        existingItem.setOwner(owner);

        Mockito.when(itemRepository.findById(10L)).thenReturn(Optional.of(existingItem));

        Item updateData = new Item();
        updateData.setName("Новое имя");

        assertThrows(NotFoundException.class, () -> itemService.update(updateData, 10L, 2L));
    }

    @Test
    void update_whenNameIsBlankOnUpdate_shouldThrowValidationException() {
        User owner = new User();
        owner.setId(1L);
        Item existingItem = new Item();
        existingItem.setId(10L);
        existingItem.setOwner(owner);

        Mockito.when(itemRepository.findById(10L)).thenReturn(Optional.of(existingItem));

        Item updateData = new Item();
        updateData.setName("");

        assertThrows(ValidationException.class, () -> itemService.update(updateData, 10L, 1L));
    }

    @Test
    void update_whenDescriptionIsBlankOnUpdate_shouldThrowValidationException() {
        User owner = new User();
        owner.setId(1L);
        Item existingItem = new Item();
        existingItem.setId(10L);
        existingItem.setOwner(owner);

        Mockito.when(itemRepository.findById(10L)).thenReturn(Optional.of(existingItem));

        Item updateData = new Item();
        updateData.setDescription("   ");

        assertThrows(ValidationException.class, () -> itemService.update(updateData, 10L, 1L));
    }
}
