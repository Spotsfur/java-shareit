package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void findOne_whenRequestNotFound_shouldThrowNotFoundException() {
        Mockito.lenient().when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        Mockito.lenient().when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.findOne(100L, 1L));
    }

    @Test
    void findOne_whenUserNotFound_shouldThrowNotFoundException() {
        Mockito.lenient().when(userRepository.existsById(anyLong())).thenReturn(false);
        Mockito.lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.findOne(100L, 1L));
    }

    //Это надо чтобы работали все моки
    private Long anyLong() {
        return Mockito.anyLong();
    }
}
