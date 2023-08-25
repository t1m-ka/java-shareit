package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.*;

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
public class BookingServiceImplTest {
    private final EntityManager em;

    private final BookingServiceImpl bookingService;

    private final UserRepository userRepository;

    private final ItemService itemService;

    private BookingDto bookingDto;

    private long booker1Id;

    private long owner1Id;

    private ItemDto itemDto;

    private long itemDtoId;

    @BeforeEach
    void setUp() {
        User owner = new User("owner1", "owner1@mail.ru");
        owner1Id = userRepository.save(owner).getId();

        User booker = new User("booker1", "booker1@mail.ru");
        booker1Id = userRepository.save(booker).getId();

        itemDto = new ItemDto(
                "thing",
                "very important thing",
                true,
                1L
        );

        itemDtoId = itemService.addItem(itemDto, owner1Id).getId();
        itemDto.setId(itemDtoId);

        bookingDto = new BookingDto(
                1L,
                itemDtoId,
                "2024-10-10T12:00:00",
                "2024-10-11T14:00:00",
                BookingStatus.WAITING,
                new UserDto(1L, "booker", "booker@book.ru"),
                itemDto
        );
    }

    @Test
    void bookItem() {
        long bookingId = bookingService.bookItem(bookingDto, booker1Id).getId();

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking returnedBooking = query.setParameter("id", bookingId).getSingleResult();

        assertThat(returnedBooking.getId(), notNullValue());
        assertThat(returnedBooking.getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedBooking.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(DATE_TIME_FORMATTER.format(returnedBooking.getStart()), equalTo(bookingDto.getStart()));
    }

    @Test
    void bookItemWithWrongItemShouldThrowException() {
        bookingDto.setItemId(1000L);
        assertThrows(ItemNotFoundException.class, () -> {
            bookingService.bookItem(bookingDto, booker1Id);
        });
    }

    @Test
    void bookItemWhenItemUnavailableShouldThrowException() {
        itemDto.setAvailable(false);
        itemService.updateItem(itemDto, itemDtoId, owner1Id);

        assertThrows(BookingUnavailableException.class, () -> {
            bookingService.bookItem(bookingDto, booker1Id);
        });
    }

    @Test
    void approveBooking() {
        long bookingId = bookingService.bookItem(bookingDto, booker1Id).getId();
        bookingService.approveBooking(bookingId, true, owner1Id);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking returnedBooking = query.setParameter("id", bookingId).getSingleResult();

        assertThat(returnedBooking.getId(), notNullValue());
        assertThat(returnedBooking.getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void rejectBooking() {
        long bookingId = bookingService.bookItem(bookingDto, booker1Id).getId();
        bookingService.approveBooking(bookingId, false, owner1Id);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking returnedBooking = query.setParameter("id", bookingId).getSingleResult();

        assertThat(returnedBooking.getId(), notNullValue());
        assertThat(returnedBooking.getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedBooking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void approveBookingWhenWrongBookingIdShouldThrowException() {
        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.approveBooking(1000L, true, owner1Id);
        });
    }

    @Test
    void approveBookingByOtherUserShouldThrowException() {
        long bookingId = bookingService.bookItem(bookingDto, booker1Id).getId();
        assertThrows(OwnershipAccessException.class, () -> {
            bookingService.approveBooking(bookingId, true, booker1Id);
        });
    }

    @Test
    void approveBookingRepeatShouldThrowException() {
        long bookingId = bookingService.bookItem(bookingDto, booker1Id).getId();
        bookingService.approveBooking(bookingId, true, owner1Id);

        assertThrows(BookingStatusException.class, () -> {
            bookingService.approveBooking(bookingId, true, owner1Id);
        });
    }

    @Test
    void getBookingInfoByBookingId() {
        long bookingId = bookingService.bookItem(bookingDto, booker1Id).getId();

        BookingDto returnedBookingDto = bookingService.getBookingInfoByBookingId(bookingId, owner1Id);

        assertThat(returnedBookingDto.getId(), notNullValue());
        assertThat(returnedBookingDto.getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedBookingDto.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedBookingDto.getStart(), equalTo(bookingDto.getStart()));
    }

    @Test
    void getBookingInfoByBookingIdWithWrongIdShouldThrowException() {
        bookingService.bookItem(bookingDto, booker1Id);

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.getBookingInfoByBookingId(1000L, booker1Id);
        });
    }

    @Test
    void getBookingInfoByBookingIdByOtherUserShouldThrowException() {
        long bookingId = bookingService.bookItem(bookingDto, booker1Id).getId();

        User otherUser = new User("user1", "user1@mail.ru");
        long otherUserId = userRepository.save(otherUser).getId();

        assertThrows(OwnershipAccessException.class, () -> {
            bookingService.getBookingInfoByBookingId(bookingId, otherUserId);
        });
    }

    @Test
    void getBookingUserListByStateAll() {
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingUserListByState("ALL", booker1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingUserListByStateFuture() {
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingUserListByState("FUTURE", booker1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingUserListByStatePast() {
        bookingDto.setStart("2022-10-10T12:00:00");
        bookingDto.setEnd("2022-10-11T14:00:00");
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingUserListByState("PAST", booker1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingUserListByStateCurrent() {
        bookingDto.setStart("2023-08-23T12:00:00");
        bookingDto.setEnd("2023-10-11T14:00:00");
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingUserListByState("CURRENT", booker1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingUserListByStateWaiting() {
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingUserListByState("WAITING", booker1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingUserListByUnknownStateShouldThrowException() {
        assertThrows(BookingStatusException.class, () -> {
            bookingService.getBookingUserListByState("BOOKING", booker1Id, 0, 10);
        });
    }

    @Test
    void getBookingUserListByStateByOtherUserShouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> {
            bookingService.getBookingUserListByState("BOOKING", 1000L, 0, 10);
        });
    }

    @Test
    void getBookingItemsByOwnerAllState() {
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingItemsByOwner("ALL", owner1Id, null, null);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingItemsByOwnerFutureState() {
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingItemsByOwner("FUTURE", owner1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingItemsByOwnerPastState() {
        bookingDto.setStart("2022-10-10T12:00:00");
        bookingDto.setEnd("2022-10-11T14:00:00");
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingItemsByOwner("PAST", owner1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingItemsByOwnerCurrentState() {
        bookingDto.setStart("2023-08-23T12:00:00");
        bookingDto.setEnd("2023-10-11T14:00:00");
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingItemsByOwner("CURRENT", owner1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingItemsByOwnerWaitingState() {
        bookingService.bookItem(bookingDto, booker1Id);

        List<BookingDto> returnedList = bookingService.getBookingItemsByOwner("WAITING", owner1Id, 0, 10);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getItem().getId(), equalTo(bookingDto.getItem().getId()));
        assertThat(returnedList.get(0).getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(returnedList.get(0).getStart(), equalTo(bookingDto.getStart()));
        assertThat(returnedList.get(0).getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingItemsByOwnerUnknownStateShouldThrowException() {
        assertThrows(BookingStatusException.class, () -> {
            bookingService.getBookingItemsByOwner("BOOKING", owner1Id, 0, 10);
        });
    }
}
