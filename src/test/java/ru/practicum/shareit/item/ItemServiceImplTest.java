package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.exception.BookingUnavailableException;
import ru.practicum.shareit.util.exception.ItemNotFoundException;
import ru.practicum.shareit.util.exception.OwnershipAccessException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.util.Util.DATE_TIME_FORMATTER;

@Transactional
@SpringBootTest(
        properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final EntityManager em;

    private final ItemServiceImpl itemService;

    private final UserServiceImpl userService;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private ItemDto itemDto;

    private User user1;

    private long user1Id;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(
                1L,
                "thing",
                "very important thing",
                true,
                1L
        );

        user1 = new User("user1", "user1@mail.ru");
        user1Id = userService.addUser(user1).getId();
    }

    @Test
    void addItem() {
        itemService.addItem(itemDto, user1Id);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item returnedItem = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(returnedItem.getId(), notNullValue());
        assertThat(returnedItem.getName(), equalTo(itemDto.getName()));
        assertThat(returnedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(returnedItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void addItemWithWrongUserShouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> {
            itemService.addItem(itemDto, 1000L);
        });
    }

    @Test
    void updateItem() {
        long itemId = itemService.addItem(itemDto, user1Id).getId();

        ItemDto updatedItemDto = new ItemDto(
                itemId,
                "updatedThing",
                "very important updatedThing",
                true,
                1L);

        itemService.updateItem(updatedItemDto, itemId, user1Id);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item returnedItem = query.setParameter("name", updatedItemDto.getName()).getSingleResult();

        assertThat(returnedItem.getId(), notNullValue());
        assertThat(returnedItem.getName(), equalTo(updatedItemDto.getName()));
        assertThat(returnedItem.getDescription(), equalTo(updatedItemDto.getDescription()));
        assertThat(returnedItem.getAvailable(), equalTo(updatedItemDto.getAvailable()));
    }

    @Test
    void updateItemWithNotByOwnerShouldThrowException() {
        long itemId = itemService.addItem(itemDto, user1Id).getId();

        long otherUserId = userService.addUser(new User("user2", "user2@mail.ru")).getId();

        assertThrows(OwnershipAccessException.class, () -> {
            itemService.updateItem(itemDto, itemId, otherUserId);
        });
    }

    @Test
    void updateUnknownItemShouldThrowException() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.updateItem(itemDto, 1000, user1.getId());
        });
    }

    @Test
    void getItemById() {
        long itemId = itemService.addItem(itemDto, user1Id).getId();

        ItemDtoWithBookingAndComments returnedItem = itemService.getItemById(itemId, user1Id);

        assertThat(returnedItem.getId(), notNullValue());
        assertThat(returnedItem.getName(), equalTo(itemDto.getName()));
        assertThat(returnedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(returnedItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void getOwnerItems() {
        itemService.addItem(itemDto, user1Id);

        List<ItemDtoWithBookingAndComments> resultItemList = itemService.getOwnerItems(user1.getId(), 0, 5);

        assertThat(resultItemList, hasSize(1));
        assertThat(resultItemList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(resultItemList.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(resultItemList.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void searchItemsByName() {
        itemService.addItem(itemDto, user1Id);

        List<ItemDto> resultItemList = itemService.searchItemsByName("thing", 0, 5);

        assertThat(resultItemList, hasSize(1));
        assertThat(resultItemList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(resultItemList.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(resultItemList.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void searchItemsByNameShouldReturnEmptyList() {
        itemService.addItem(itemDto, user1Id);

        List<ItemDto> resultItemList = itemService.searchItemsByName("", 0, 5);

        assertThat(resultItemList, hasSize(0));
    }

    @Test
    void addCommentItem() {
        long itemId = itemService.addItem(itemDto, user1Id).getId();

        Item returnedItem = itemRepository.findById(itemId).get();

        User user2 = new User("user2", "user2@mail.ru");
        userService.addUser(user2);

        Booking booking = new Booking(
                LocalDateTime.parse("2023-08-01T10:00:00", DATE_TIME_FORMATTER),
                LocalDateTime.parse("2023-08-01T12:00:00", DATE_TIME_FORMATTER),
                user2,
                returnedItem,
                BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(1L, "cool thing", "user2", "2023-08-18T14:00:00");

        itemService.addCommentItem(itemId, user2.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment returnedComment = query.setParameter("text", commentDto.getText()).getSingleResult();

        assertThat(returnedComment.getId(), notNullValue());
        assertThat(returnedComment.getText(), equalTo(commentDto.getText()));
        assertThat(returnedComment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));
    }

    @Test
    void addCommentItemByWrongUserShouldThrowException() {
        long itemId = itemService.addItem(itemDto, user1Id).getId();

        User user2 = new User("user2", "user2@mail.ru");
        userService.addUser(user2);
        CommentDto commentDto = new CommentDto(1L, "cool thing", "user1", "2023-08-18T14:00:00");

        assertThrows(BookingUnavailableException.class, () -> {
            itemService.addCommentItem(itemId, user2.getId(), commentDto);
        });
    }
}
