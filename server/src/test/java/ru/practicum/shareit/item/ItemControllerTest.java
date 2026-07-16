package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInfo;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private Item validItem;
    private ItemInfo validItemInfo;
    private Comment validComment;
    private ItemDto validItemDto;

    @BeforeEach
    void setUp() {
        validItem = new Item();
        validItem.setId(1L);
        validItem.setName("Молоток");
        validItem.setDescription("Слесарный молоток");
        validItem.setAvailable(true);

        validItemInfo = ItemInfo.builder()
                .item(validItem)
                .comments(List.of())
                .build();

        validComment = new Comment();
        validComment.setId(1L);
        validComment.setText("Хороший инструмент");

        validItemDto = ItemDto.builder()
                .id(1L)
                .name("Молоток")
                .description("Слесарный молоток")
                .available(true)
                .build();
    }

    @Test
    void create_shouldReturnItemAndStatusOk() throws Exception {
        Mockito.when(itemService.create(any(Item.class), eq(1L)))
                .thenReturn(validItem);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Молоток"));
    }

    @Test
    void findAllByUserId_shouldReturnListAndStatusOk() throws Exception {
        Mockito.when(itemService.findAllByUserId(1L))
                .thenReturn(List.of(validItemInfo));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Молоток"));
    }

    @Test
    void findOne_shouldReturnItemInfoAndStatusOk() throws Exception {
        Mockito.when(itemService.findOne(1L))
                .thenReturn(validItemInfo);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Молоток"));
    }

    @Test
    void findByText_shouldReturnListAndStatusOk() throws Exception {
        Mockito.when(itemService.findByText(anyString(), anyLong()))
                .thenReturn(List.of(validItem));

        mockMvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, 1L)
                        .param("text", "Молоток"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void update_shouldReturnUpdatedItemAndStatusOk() throws Exception {
        Mockito.when(itemService.update(any(Item.class), eq(1L), eq(1L)))
                .thenReturn(validItem);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createComment_shouldReturnCommentAndStatusOk() throws Exception {
        CommentIncomingDto incomingDto = new CommentIncomingDto();
        incomingDto.setText("Хороший инструмент");

        Mockito.when(itemService.createComment(any(Comment.class), eq(1L), eq(1L)))
                .thenReturn(validComment);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Хороший инструмент"));
    }
}
