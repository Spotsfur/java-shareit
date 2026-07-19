package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private ItemDto validItemDto;

    @BeforeEach
    void setUp() {
        validItemDto = ItemDto.builder()
                .id(1L)
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .build();
    }

    @Test
    void create_shouldReturnStatusOk() throws Exception {
        Mockito.when(itemClient.createItem(eq(1L), any(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(validItemDto, HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByUserId_shouldReturnStatusOk() throws Exception {
        Mockito.when(itemClient.findAllByUserId(1L))
                .thenReturn(new ResponseEntity<>(List.of(validItemDto), HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findOne_shouldReturnStatusOk() throws Exception {
        Mockito.when(itemClient.getItem(1L, 1L))
                .thenReturn(new ResponseEntity<>(validItemDto, HttpStatus.OK));

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findByText_shouldReturnStatusOk() throws Exception {
        Mockito.when(itemClient.searchItems("Отвертка", 1L))
                .thenReturn(new ResponseEntity<>(List.of(validItemDto), HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, 1L)
                        .param("text", "Отвертка"))
                .andExpect(status().isOk());
    }

    @Test
    void update_shouldReturnStatusOk() throws Exception {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name(validItemDto.getName())
                .description(validItemDto.getDescription())
                .available(validItemDto.getAvailable())
                .build();

        Mockito.when(itemClient.updateItem(eq(1L), eq(1L), any(ItemUpdateDto.class)))
                .thenReturn(new ResponseEntity<>(validItemDto, HttpStatus.OK));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createComment_whenCommentIsValid_shouldReturnStatusOk() throws Exception {
        CommentIncomingDto commentDto = new CommentIncomingDto();
        commentDto.setText("Отличная отвертка, рекомендую!");

        Mockito.when(itemClient.createComment(eq(1L), eq(1L), any(CommentIncomingDto.class)))
                .thenReturn(new ResponseEntity<>(commentDto, HttpStatus.OK));

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createComment_whenTextIsBlank_shouldReturnBadRequest() throws Exception {
        CommentIncomingDto invalidCommentDto = new CommentIncomingDto();
        invalidCommentDto.setText(""); // Нарушаем @NotBlank

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommentDto)))
                .andExpect(status().isBadRequest());
    }
}
