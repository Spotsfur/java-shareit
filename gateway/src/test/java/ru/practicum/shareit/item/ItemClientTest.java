package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemClientTest {

    private ItemClient itemClient;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;
    private ItemDto itemDto;
    private static final String BASE_URL = "http://localhost:9090/items";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        RestTemplateBuilder builder = new RestTemplateBuilder();

        itemClient = new ItemClient("http://localhost:9090", builder);
        RestTemplate internalRestTemplate = (RestTemplate) ReflectionTestUtils.getField(itemClient, "rest");
        mockServer = MockRestServiceServer.createServer(internalRestTemplate);

        itemDto = ItemDto.builder().id(1L).name("Дрель").description("Ударная").available(true).build();
    }

    @Test
    void createItem_shouldSendPostRequestAndReturnOk() throws Exception {
        String jsonDto = objectMapper.writeValueAsString(itemDto);

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json(jsonDto))
                .andRespond(withSuccess(jsonDto, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.createItem(1L, itemDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void findAllByUserId_shouldSendGetRequestAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.findAllByUserId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getItem_shouldSendGetRequestWithIdAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getItem(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void searchItems_shouldSendGetRequestWithQueryParamAndReturnOk() {
        String expectedUrl = org.springframework.web.util.UriComponentsBuilder
                .fromHttpUrl(BASE_URL + "/search")
                .queryParam("text", "дрель")
                .encode()
                .build()
                .toUriString();

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.searchItems("дрель", 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }


    @Test
    void updateItem_shouldSendPatchRequestAndReturnOk() throws Exception {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(updateDto);
        String jsonResponse = objectMapper.writeValueAsString(itemDto);

        mockServer.expect(requestTo(BASE_URL + "/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json(jsonRequest))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.updateItem(1L, 1L, updateDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void createComment_shouldSendPostRequestToCommentPathAndReturnOk() throws Exception {
        CommentIncomingDto commentDto = new CommentIncomingDto();
        commentDto.setText("Супер вещь!");
        String jsonComment = objectMapper.writeValueAsString(commentDto);

        mockServer.expect(requestTo(BASE_URL + "/1/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json(jsonComment))
                .andRespond(withSuccess(jsonComment, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.createComment(1L, 1L, commentDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }
}
