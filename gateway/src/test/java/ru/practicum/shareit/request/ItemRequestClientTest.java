package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemRequestClientTest {

    private ItemRequestClient requestClient;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;
    private ItemRequestDto requestDto;
    private static final String BASE_URL = "http://localhost:9090/requests";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        RestTemplateBuilder builder = new RestTemplateBuilder();

        requestClient = new ItemRequestClient("http://localhost:9090", builder);
        RestTemplate internalRestTemplate = (RestTemplate) ReflectionTestUtils.getField(requestClient, "rest");
        mockServer = MockRestServiceServer.createServer(internalRestTemplate);

        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна щётка по металлу")
                .build();
    }

    @Test
    void createRequest_shouldSendPostRequestAndReturnOk() throws Exception {
        String jsonDto = objectMapper.writeValueAsString(requestDto);

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json(jsonDto))
                .andRespond(withSuccess(jsonDto, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = requestClient.createRequest(1L, requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getOwnRequests_shouldSendGetRequestAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = requestClient.getOwnRequests(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getAllRequestsOfOthers_shouldSendGetToAllPathAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL + "/all"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = requestClient.getAllRequestsOfOthers(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getRequestById_shouldSendGetWithIdAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = requestClient.getRequestById(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }
}
