package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private Booking incomingBookingMock;
    private BookingIncomingDto validIncomingDto;

    @BeforeEach
    void setUp() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Букер");

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        incomingBookingMock = new Booking();
        incomingBookingMock.setId(1L);
        incomingBookingMock.setStart(LocalDateTime.now().plusDays(1));
        incomingBookingMock.setEnd(LocalDateTime.now().plusDays(2));
        incomingBookingMock.setStatus(BookingStatus.WAITING);
        incomingBookingMock.setItem(item);
        incomingBookingMock.setBooker(booker);

        validIncomingDto = new BookingIncomingDto();
        validIncomingDto.setItemId(10L);
        validIncomingDto.setStart(LocalDateTime.now().plusDays(1));
        validIncomingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_shouldReturnBookingResponseDtoAndStatusOk() throws Exception {
        Mockito.when(bookingService.create(any(Booking.class), eq(10L), eq(1L)))
                .thenReturn(incomingBookingMock);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validIncomingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(10))
                .andExpect(jsonPath("$.booker.id").value(1));
    }

    @Test
    void approve_shouldReturnBookingResponseDtoAndStatusOk() throws Exception {
        incomingBookingMock.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingService.approve(eq(1L), eq(1L), eq(true)))
                .thenReturn(incomingBookingMock);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void findOne_shouldReturnBookingResponseDtoAndStatusOk() throws Exception {
        Mockito.when(bookingService.findOne(1L, 1L))
                .thenReturn(incomingBookingMock);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"));
    }

    @Test
    void findAllByBooker_shouldReturnListResponseDtoAndStatusOk() throws Exception {
        Mockito.when(bookingService.findAllByBooker(eq(1L), anyString()))
                .thenReturn(List.of(incomingBookingMock));

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(10));
    }

    @Test
    void findAllByOwner_shouldReturnListResponseDtoAndStatusOk() throws Exception {
        Mockito.when(bookingService.findAllByOwner(eq(1L), anyString()))
                .thenReturn(List.of(incomingBookingMock));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(10));
    }
}
