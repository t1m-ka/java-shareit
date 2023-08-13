package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query("select booking "
            + "from Booking as booking "
            + "join booking.booker as booker "
            + "where (booking.status = ?1) "
            + "and booker.id = ?2 "
            + "order by booking.start desc")
    List<Booking> findAllBookingUserByState(BookingStatus bookingStatus, long userId);

/*    @Query("select booking "
            + "from Booking as booking "
            + "join booking.booker as booker "
            + "where booker.id = ?1 "
            + "order by booking.start desc")*/
    List<Booking> findAllByBookerId(long userId, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(long userId, LocalDateTime now, Sort sort);

    @Query("select booking "
            + "from Booking as booking "
            + "join booking.booker as booker "
            + "where booking.start <= ?1 "
            + "and booking.end >= ?1 "
            + "and booker.id = ?2 "
            + "order by booking.start desc")
    List<Booking> findAllCurrentBookingUser(LocalDateTime now, long userId);

    @Query("select booking "
            + "from Booking as booking "
            + "join booking.item as it "
            + "join it.owner as ow "
            + "where (booking.status = ?1) "
            + "and ow.id = ?2")
    List<Booking> findAllBookingItemsByOwner(BookingStatus bookingStatus, long ownerId);

    List<Booking> findByItemOwnerId(long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime now, Sort sort);

    @Query("select booking "
            + "from Booking as booking "
            + "join booking.item as it "
            + "join it.owner as ow "
            + "where booking.start <= ?1 "
            + "and booking.end >= ?1 "
            + "and ow.id = ?2 "
            + "order by booking.start desc")
    List<Booking> findAllCurrentBookingItemsByOwner(LocalDateTime now, long userId);

    @Query("select booking "
            + "from Booking as booking "
            + "join booking.item as it "
            + "where it.id = ?1 "
            + "and (booking.start between ?2 and ?3) "
            + "or (booking.end between ?2 and ?3)")
    List<Booking> findItemBookingBetweenDate(long itemId, LocalDateTime startDate, LocalDateTime endDate);
}
