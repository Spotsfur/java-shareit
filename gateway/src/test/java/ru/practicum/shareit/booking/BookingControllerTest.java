package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingIncomingDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private BookingIncomingDto validDto;

    @BeforeEach
    void setUp() {
        validDto = new BookingIncomingDto();
        validDto.setItemId(1L);
        validDto.setStart(LocalDateTime.now().plusDays(1));
        validDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_whenValid_shouldReturnStatusOk() throws Exception {
        Mockito.when(bookingClient.bookItem(anyLong(), any(BookingIncomingDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk());
    }

    @Test
    void approve_shouldReturnStatusOk() throws Exception {
        Mockito.when(bookingClient.approveBooking(anyLong(), anyLong(), any(Boolean.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void findOne_shouldReturnStatusOk() throws Exception {
        Mockito.when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByBooker_whenStateIsValid_shouldReturnStatusOk() throws Exception {
        Mockito.when(bookingClient.getBookingsByBooker(anyLong(), anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "WAITING"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByBooker_whenStateIsUnknown_shouldThrowIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllByOwner_whenStateIsValid_shouldReturnStatusOk() throws Exception {
        Mockito.when(bookingClient.getBookingsByOwner(anyLong(), anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByOwner_whenStateIsUnknown_shouldThrowIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "INVALID_STATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenStartIsNull_shouldReturnBadRequest() throws Exception {
        validDto.setStart(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenEndIsNull_shouldReturnBadRequest() throws Exception {
        validDto.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenEndIsBeforeStart_shouldReturnBadRequest() throws Exception {
        validDto.setStart(LocalDateTime.now().plusDays(5));
        validDto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenStartInPast_shouldReturnBadRequest() throws Exception {
        validDto.setStart(LocalDateTime.now().minusDays(5));
        validDto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_whenItemIdIsNull_shouldReturnBadRequest() throws Exception {
        validDto.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest());
    }

}
