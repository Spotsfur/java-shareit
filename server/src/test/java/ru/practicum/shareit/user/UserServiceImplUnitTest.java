package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void create_whenNameIsNull_shouldThrowValidationException() {
        User user = new User();
        user.setEmail("user@mail.ru");
        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void create_whenEmailIsNull_shouldThrowValidationException() {
        User user = new User();
        user.setName("User");
        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void update_whenIdIsNull_shouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> userService.update(new User(), null));
    }

    @Test
    void update_whenEmailIsBlank_shouldThrowValidationException() {
        User existing = new User();
        existing.setId(1L);
        org.mockito.Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));

        User updateData = new User();
        updateData.setEmail("");
        assertThrows(ValidationException.class, () -> userService.update(updateData, 1L));
    }

    @Test
    void update_whenNameIsBlank_shouldThrowValidationException() {
        User existing = new User();
        existing.setId(1L);
        org.mockito.Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));

        User updateData = new User();
        updateData.setName("");
        assertThrows(ValidationException.class, () -> userService.update(updateData, 1L));
    }
}
