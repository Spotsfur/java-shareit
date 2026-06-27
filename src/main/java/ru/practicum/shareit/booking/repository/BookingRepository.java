package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //Методы по букеру
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(
            Long bookerId, LocalDateTime nowStart, LocalDateTime nowEnd);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    //Методы по владельцу
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(
            Long ownerId, LocalDateTime nowStart, LocalDateTime nowEnd);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    //Поиск approved бронирований - ближайшее прошлое
    List<Booking> findByItemIdAndStatusAndStartBeforeOrderByEndDesc(Long itemId, BookingStatus status, LocalDateTime now);

    //Поиск approved бронирований - ближайшее будущее
    List<Booking> findByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, BookingStatus status, LocalDateTime now);

    //Approved бронирования по id вещи
    List<Booking> findByItemIdAndStatusOrderByStartDesc(Long itemId, BookingStatus status);

    //Approved бронирования по списку вещей
    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Long> itemIds, BookingStatus status);


}
