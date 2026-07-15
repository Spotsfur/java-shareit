package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requestor;
    private User otherUser;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("Алексей");
        requestor.setEmail("alex@mail.ru");
        requestor = userRepository.save(requestor);

        otherUser = new User();
        otherUser.setName("Дмитрий");
        otherUser.setEmail("dima@mail.ru");
        otherUser = userRepository.save(otherUser);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Ищу перфоратор на 2 дня");
    }

    @Test
    void create_whenValid_shouldSaveRequestWithCreationTime() {
        ItemRequest saved = requestService.create(itemRequest, requestor.getId());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Ищу перфоратор на 2 дня");
        assertThat(saved.getRequestor().getId()).isEqualTo(requestor.getId());
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    void create_whenUserDoesNotExist_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                requestService.create(itemRequest, 999L));
    }

    @Test
    void create_whenDescriptionIsEmpty_shouldThrowValidationException() {
        itemRequest.setDescription("");

        assertThrows(ValidationException.class, () ->
                requestService.create(itemRequest, requestor.getId()));
    }

    @Test
    void findAllByUserId_shouldReturnOnlyOwnRequests() {
        requestService.create(itemRequest, requestor.getId());

        ItemRequest otherRequest = new ItemRequest();
        otherRequest.setDescription("Нужна стремянка");
        requestService.create(otherRequest, otherUser.getId());

        List<ItemRequest> ownRequests = requestService.findAllByUserId(requestor.getId());
        assertThat(ownRequests).hasSize(1);
        assertThat(ownRequests.get(0).getDescription()).isEqualTo("Ищу перфоратор на 2 дня");
    }

    @Test
    void findAllOfOthers_shouldReturnRequestsFromOtherUsersSorted() throws InterruptedException {
        requestService.create(itemRequest, requestor.getId());
        Thread.sleep(10);

        ItemRequest secondRequest = new ItemRequest();
        secondRequest.setDescription("Нужна газонокосилка");
        requestService.create(secondRequest, requestor.getId());

        List<ItemRequest> othersRequests = requestService.findAllOfOthers(otherUser.getId());

        assertThat(othersRequests).hasSize(2);
        assertThat(othersRequests.get(0).getDescription()).isEqualTo("Нужна газонокосилка");
        assertThat(othersRequests.get(1).getDescription()).isEqualTo("Ищу перфоратор на 2 дня");
    }

    @Test
    void findOne_whenRequestExists_shouldReturnRequest() {
        ItemRequest saved = requestService.create(itemRequest, requestor.getId());

        ItemRequest found = requestService.findOne(saved.getId(), requestor.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getDescription()).isEqualTo("Ищу перфоратор на 2 дня");
    }

    @Test
    void findOne_whenRequestDoesNotExist_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                requestService.findOne(999L, requestor.getId()));
    }

    @Test
    void findItemsForRequests_shouldReturnAllItemsMatchingRequests() {
        ItemRequest savedRequest = requestService.create(itemRequest, requestor.getId());

        Item item = new Item();
        item.setName("Перфоратор Bosch");
        item.setDescription("Мощный, с бурами");
        item.setAvailable(true);
        item.setOwner(otherUser);
        item.setRequest(savedRequest);
        itemRepository.save(item);

        List<Item> matchingItems = requestService.findItemsForRequests(List.of(savedRequest));

        assertThat(matchingItems).hasSize(1);
        assertThat(matchingItems.get(0).getName()).isEqualTo("Перфоратор Bosch");
        assertThat(matchingItems.get(0).getRequest().getId()).isEqualTo(savedRequest.getId());
    }
}
