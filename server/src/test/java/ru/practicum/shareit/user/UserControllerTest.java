package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setId(1L);
        validUser.setName("Петр Петров");
        validUser.setEmail("petr@yandex.ru");
    }

    @Test
    void create_shouldReturnUserAndStatusOk() throws Exception {
        Mockito.when(userService.create(any(User.class)))
                .thenReturn(validUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Петр Петров"))
                .andExpect(jsonPath("$.email").value("petr@yandex.ru"));
    }

    @Test
    void findOne_shouldReturnUserAndStatusOk() throws Exception {
        Mockito.when(userService.findOne(1L))
                .thenReturn(validUser);

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Петр Петров"))
                .andExpect(jsonPath("$.email").value("petr@yandex.ru"));
    }

    @Test
    void update_shouldReturnUpdatedUserAndStatusOk() throws Exception {
        Mockito.when(userService.update(any(User.class), eq(1L)))
                .thenReturn(validUser);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Петр Петров"));
    }

    @Test
    void delete_shouldReturnStatusOk() throws Exception {
        // Правильный синтаксис Mockito для void-методов:
        Mockito.doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }
}
