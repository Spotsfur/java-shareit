package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BookingClientTest {

    private BookingClient bookingClient;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;
    private BookingIncomingDto incomingDto;
    private static final String BASE_URL = "http://localhost:9090/bookings";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RestTemplateBuilder builder = new RestTemplateBuilder();

        bookingClient = new BookingClient("http://localhost:9090", builder);
        RestTemplate internalRestTemplate = (RestTemplate) ReflectionTestUtils.getField(bookingClient, "rest");
        mockServer = MockRestServiceServer.createServer(internalRestTemplate);

        incomingDto = new BookingIncomingDto();
        incomingDto.setItemId(1L);
        incomingDto.setStart(LocalDateTime.now().plusDays(1));
        incomingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void bookItem_shouldSendPostRequestAndReturnOk() throws Exception {
        String jsonDto = objectMapper.writeValueAsString(incomingDto);

        mockServer.expect(requestTo(BASE_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().json(jsonDto))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.bookItem(1L, incomingDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void approveBooking_shouldSendPatchWithQueryParamAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL + "/1?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.approveBooking(1L, 1L, true);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getBooking_shouldSendGetWithIdAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBooking(1L, 1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getBookingsByBooker_shouldSendGetWithStateQueryAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL + "?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookingsByBooker(1L, "ALL");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getBookingsByOwner_shouldSendGetWithWithOwnerPathAndQueryAndReturnOk() {
        mockServer.expect(requestTo(BASE_URL + "/owner?state=WAITING"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookingsByOwner(1L, "WAITING");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void testBookingStatusEnumCoverage() {
        for (BookingStatus status : BookingStatus.values()) {
            assertThat(BookingStatus.valueOf(status.name())).isEqualTo(status);
        }
    }

}
