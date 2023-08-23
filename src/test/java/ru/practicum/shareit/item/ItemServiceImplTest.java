package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.exception.ItemNotFoundException;
import ru.practicum.shareit.util.exception.OwnershipAccessException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final EntityManager em;

    private final ItemServiceImpl itemService;

    private final UserServiceImpl userService;

    private ItemDto itemDto;

    private User user1;

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
        userService.addUser(user1);
    }

    @Test
    void addItem() {
        itemService.addItem(itemDto, user1.getId());

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
        long itemId = itemService.addItem(itemDto, user1.getId()).getId();

        ItemDto updatedItemDto = new ItemDto(
                itemId,
                "updatedThing",
                "very important updatedThing",
                true,
                1L);

        itemService.updateItem(updatedItemDto, user1.getId(), itemId);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item returnedItem = query.setParameter("name", updatedItemDto.getName()).getSingleResult();

        assertThat(returnedItem.getId(), notNullValue());
        assertThat(returnedItem.getName(), equalTo(updatedItemDto.getName()));
        assertThat(returnedItem.getDescription(), equalTo(updatedItemDto.getDescription()));
        assertThat(returnedItem.getAvailable(), equalTo(updatedItemDto.getAvailable()));
    }

    @Test
    void updateItemWithNotByOwnerShouldThrowException() {
        long itemId = itemService.addItem(itemDto, user1.getId()).getId();

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
        long itemId = itemService.addItem(itemDto, user1.getId()).getId();

        ItemDtoWithBookingAndComments returnedItem = itemService.getItemById(itemId, user1.getId());

        assertThat(returnedItem.getId(), notNullValue());
        assertThat(returnedItem.getName(), equalTo(itemDto.getName()));
        assertThat(returnedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(returnedItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void getOwnerItems() {
        itemService.addItem(itemDto, user1.getId());

        List<ItemDtoWithBookingAndComments> resultItemList = itemService.getOwnerItems(user1.getId(), 0, 5);

        assertThat(resultItemList, hasSize(1));
        assertThat(resultItemList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(resultItemList.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(resultItemList.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void searchItemsByName() {
        itemService.addItem(itemDto, user1.getId());

        List<ItemDto> resultItemList = itemService.searchItemsByName("thing", 0, 5);

        assertThat(resultItemList, hasSize(1));
        assertThat(resultItemList.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(resultItemList.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(resultItemList.get(0).getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void searchItemsByNameShouldReturnEmptyList() {
        itemService.addItem(itemDto, user1.getId());

        List<ItemDto> resultItemList = itemService.searchItemsByName("", 0, 5);

        assertThat(resultItemList, hasSize(0));
    }

    @Test
    void addCommentItem() {
        long itemId = itemService.addItem(itemDto, user1.getId()).getId();

        CommentDto commentDto = new CommentDto(1L, "cool thing", "user", "2023-08-18T14:00:00");

        itemService.addCommentItem(itemId, user1.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("Select c from Comments c where c.text = :text", Comment.class);
        Comment returnedComment = query.setParameter("text", commentDto.getText()).getSingleResult();

        assertThat(returnedComment.getId(), notNullValue());
        assertThat(returnedComment.getText(), equalTo(commentDto.getText()));
        assertThat(returnedComment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));
    }
}
