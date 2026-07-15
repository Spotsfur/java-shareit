package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ru.practicum.shareit.user.repository.UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Интеграционный Тест");
        user.setEmail("integration@yandex.ru");
    }

    @Test
    void create_whenUserIsValid_shouldSaveToDatabase() {
        User savedUser = userService.create(user);

        assertThat(savedUser.getId()).isNotNull();

        Optional<User> fromDb = userRepository.findById(savedUser.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getName()).isEqualTo("Интеграционный Тест");
        assertThat(fromDb.get().getEmail()).isEqualTo("integration@yandex.ru");
    }

    @Test
    void create_whenEmailIsInvalid_shouldThrowValidationException() {
        user.setEmail("invalid-email");

        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    void create_whenEmailDuplicate_shouldThrowConflictException() {
        userService.create(user);

        User duplicateUser = new User();
        duplicateUser.setName("Дубликат");
        duplicateUser.setEmail("integration@yandex.ru");

        assertThrows(ConflictException.class, () -> userService.create(duplicateUser));
    }

    @Test
    void findOne_whenUserExists_shouldReturnUser() {
        User savedUser = userService.create(user);

        User foundUser = userService.findOne(savedUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("Интеграционный Тест");
    }

    @Test
    void findOne_whenUserDoesNotExist_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.findOne(999L));
    }

    @Test
    void update_whenValid_shouldUpdateFieldsInDatabase() {
        User savedUser = userService.create(user);

        User updateData = new User();
        updateData.setName("Обновленное Имя");
        updateData.setEmail("updated@yandex.ru");

        User updatedUser = userService.update(updateData, savedUser.getId());

        assertThat(updatedUser.getName()).isEqualTo("Обновленное Имя");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@yandex.ru");

        User fromDb = userService.findOne(savedUser.getId());
        fromDb.setName("Обновленное Имя");
    }

    @Test
    void delete_whenUserExists_shouldRemoveFromDatabase() {
        User savedUser = userService.create(user);

        userService.delete(savedUser.getId());

        assertThrows(NotFoundException.class, () -> userService.findOne(savedUser.getId()));
    }
}
