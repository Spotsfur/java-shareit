package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class UserClientTest {

    private UserClient userClient;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        RestTemplateBuilder builder = new RestTemplateBuilder();

        userClient = new UserClient("http://localhost:9090", builder);

        RestTemplate internalRestTemplate = (RestTemplate) ReflectionTestUtils.getField(userClient, "rest");

        mockServer = MockRestServiceServer.createServer(internalRestTemplate);

        userDto = UserDto.builder().id(1L).name("Ivan").email("ivan@mail.ru").build();
    }

    @Test
    void createUser_shouldSendPostRequestAndReturnOk() throws Exception {
        String jsonDto = objectMapper.writeValueAsString(userDto);

        mockServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(jsonDto))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(jsonDto, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.createUser(userDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getUser_shouldSendGetRequestAndReturnOk() throws Exception {
        String jsonDto = objectMapper.writeValueAsString(userDto);

        mockServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonDto, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void updateUser_shouldSendPatchRequestAndReturnOk() throws Exception {
        String jsonDto = objectMapper.writeValueAsString(userDto);

        mockServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(jsonDto, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.updateUser(userDto, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }


    @Test
    void deleteUser_shouldSendDeleteRequestAndReturnOk() {
        mockServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = userClient.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void shouldHandleServerErrorsCorrectly() {
        mockServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).body("User not found"));

        ResponseEntity<Object> response = userClient.getUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        mockServer.verify();
    }
}
