package ru.practicum.shareit.request.repository;

//import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    //Запросы конкретного пользователя
    //List<ItemRequest> findAllByRequestorId(Long requestorId, Sort sort);
    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    //Все запросы кроме указанного
    //List<ItemRequest> findAllByRequestorIdNot(Long requestorId, Sort sort);
    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long requestorId);
}
