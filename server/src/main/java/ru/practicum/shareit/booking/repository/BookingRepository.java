package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select booking "
            + "from Booking as booking "
            + "join booking.booker as booker "
            + "where (booking.status = ?1) "
            + "and booker.id = ?2 "
            + "order by booking.start desc")
    List<Booking> findAllBookingUserByState(BookingStatus bookingStatus, long userId, Pageable page);

    List<Booking> findAllByBookerId(long userId, Pageable page);

    List<Booking> findByBookerIdAndStartIsAfter(long userId, LocalDateTime now, Pageable page);

    List<Booking> findByBookerIdAndEndIsBefore(long userId, LocalDateTime now, Pageable page);

    @Query("select booking "
            + "from Booking as booking "
            + "join booking.booker as booker "
            + "where booking.start <= ?1 "
            + "and booking.end >= ?1 "
            + "and booker.id = ?2 "
            + "order by booking.start asc")
    List<Booking> findAllCurrentBookingUser(LocalDateTime now, long userId, Pageable page);

    @Query("select booking "
            + "from Booking as booking "
            + "join booking.item as it "
            + "join it.owner as ow "
            + "where (booking.status = ?1) "
            + "and ow.id = ?2")
    List<Booking> findAllBookingItemsByOwner(BookingStatus bookingStatus, long ownerId, Pageable page);

    List<Booking> findByItemOwnerId(long ownerId, Pageable page);

    List<Booking> findByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime now, Pageable page);

    List<Booking> findByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime now, Pageable page);

    @Query("select booking "
            + "from Booking as booking "
            + "join booking.item as it "
            + "join it.owner as ow "
            + "where booking.start <= ?1 "
            + "and booking.end >= ?1 "
            + "and ow.id = ?2 "
            + "order by booking.start asc")
    List<Booking> findAllCurrentBookingItemsByOwner(LocalDateTime now, long userId, Pageable page);

    List<Booking> findByItemIdAndStartBeforeOrderByEndDesc(long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime now);
}
