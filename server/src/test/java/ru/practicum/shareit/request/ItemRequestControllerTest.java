package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private ItemRequest mockSavedRequest;
    private ItemRequestDto inputDto;

    @BeforeEach
    void setUp() {
        mockSavedRequest = new ItemRequest();
        mockSavedRequest.setId(1L);
        mockSavedRequest.setDescription("Нужна дрель");
        mockSavedRequest.setCreated(LocalDateTime.now());

        // DTO, который мы отправляем контроллеру
        inputDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .build();
    }

    @Test
    void create_shouldReturnRequestDtoAndStatusOk() throws Exception {
        Mockito.when(requestService.create(any(ItemRequest.class), eq(1L)))
                .thenReturn(mockSavedRequest);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void findAllByUserId_shouldReturnListDtoAndStatusOk() throws Exception {
        Mockito.when(requestService.findAllByUserId(1L))
                .thenReturn(List.of(mockSavedRequest));
        Mockito.when(requestService.findItemsForRequests(anyList()))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    @Test
    void findAllOfOthers_shouldReturnListDtoAndStatusOk() throws Exception {
        Mockito.when(requestService.findAllOfOthers(1L))
                .thenReturn(List.of(mockSavedRequest));
        Mockito.when(requestService.findItemsForRequests(anyList()))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    @Test
    void findOne_shouldReturnRequestDtoAndStatusOk() throws Exception {
        Mockito.when(requestService.findOne(eq(1L), anyLong()))
                .thenReturn(mockSavedRequest);
        Mockito.when(requestService.findItemsForRequests(anyList()))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }
}
