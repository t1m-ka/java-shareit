package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.util.Util.DATE_TIME_FORMATTER;

@Transactional
@SpringBootTest(
        properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    private final EntityManager em;

    private final ItemRequestServiceImpl requestService;

    private final UserRepository userRepository;

    private final ItemServiceImpl itemService;

    private ItemRequestDto requestDto;

    private ItemDto itemDto;

    private User requestor1;

    private long requestor1Id;

    @BeforeEach
    void setUp() {
        requestor1 = new User("requestor1", "requestor1@mail.ru");
        requestor1Id = userRepository.save(requestor1).getId();

        requestDto = requestService.addItemRequest(requestor1Id, new ItemRequestDto("Needed thing"));

        User owner = new User("owner1", "owner1@mail.ru");
        long ownerId = userRepository.save(owner).getId();

        itemDto = itemService.addItem(
                new ItemDto(
                        "thing",
                        "very important thing",
                        true,
                        1L),
                ownerId);
    }

    @Test
    void testCorrectAddItemRequest() {
        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest returnedItemRequest = query.setParameter("id", requestDto.getId()).getSingleResult();

        assertThat(returnedItemRequest.getId(), notNullValue());
        assertThat(returnedItemRequest.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(DATE_TIME_FORMATTER.format(returnedItemRequest.getCreated()), equalTo(requestDto.getCreated()));
    }

    @Test
    void testCorrectGetUserItemRequestList() {
        List<ItemRequestDtoWithAnswers> returnedList = requestService.getUserItemRequestList(requestor1Id);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getId(), equalTo(requestDto.getId()));
        assertThat(returnedList.get(0).getDescription(), equalTo(requestDto.getDescription()));
        assertThat(returnedList.get(0).getItems(), hasSize(0));
    }

    @Test
    void testGetUserItemRequestListWithWrongUserShouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> {
            requestService.getUserItemRequestList(1000L);
        });
    }

    @Test
    void testCorrectGetOtherUsersItemRequestList() {
        User requestor2 = new User("requestor2", "requestor2@mail.ru");
        long requestor2Id = userRepository.save(requestor2).getId();
        ItemRequestDto returnedDto = requestService.addItemRequest(requestor2Id, new ItemRequestDto("Second needed thing"));

        List<ItemRequestDtoWithAnswers> returnedList = requestService.getOtherUsersItemRequestList(requestor1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getId(), equalTo(returnedDto.getId()));
        assertThat(returnedList.get(0).getDescription(), equalTo(returnedDto.getDescription()));
        assertThat(returnedList.get(0).getItems(), hasSize(0));
    }

    @Test
    void testCorrectGetOtherUsersItemRequestListShouldReturnEmptyList() {
        User requestor2 = new User("requestor2", "requestor2@mail.ru");
        long requestor2Id = userRepository.save(requestor2).getId();

        List<ItemRequestDtoWithAnswers> returnedList = requestService.getOtherUsersItemRequestList(requestor2Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getId(), equalTo(requestDto.getId()));
        assertThat(returnedList.get(0).getDescription(), equalTo(requestDto.getDescription()));
        assertThat(returnedList.get(0).getItems().get(0).getName(), equalTo(itemDto.getName()));
    }
}
