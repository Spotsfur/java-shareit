package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private UserDto validUserDto;

    @BeforeEach
    void setUp() {
        validUserDto = UserDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@yandex.ru")
                .build();
    }

    @Test
    void create_whenUserIsValid_shouldReturnStatusOk() throws Exception {
        Mockito.when(userClient.createUser(any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(validUserDto, HttpStatus.OK));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isOk());
    }

    @Test
    void create_whenNameIsBlank_shouldReturnBadRequest() throws Exception {
        UserDto invalidUser = UserDto.builder()
                .name("")
                .email("ivan@yandex.ru")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenEmailIsInvalid_shouldReturnBadRequest() throws Exception {
        UserDto invalidUser = UserDto.builder()
                .name("Иван")
                .email("не-email")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findOne_shouldReturnStatusOk() throws Exception {
        Mockito.when(userClient.getUser(1L))
                .thenReturn(new ResponseEntity<>(validUserDto, HttpStatus.OK));

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturnStatusOk() throws Exception {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name(validUserDto.getName())
                .email(validUserDto.getEmail())
                .build();

        Mockito.when(userClient.updateUser(any(UserUpdateDto.class), eq(1L)))
                .thenReturn(new ResponseEntity<>(validUserDto, HttpStatus.OK));

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnStatusOk() throws Exception {
        Mockito.when(userClient.deleteUser(1L))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }
}
