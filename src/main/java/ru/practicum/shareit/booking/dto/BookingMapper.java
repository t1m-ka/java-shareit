package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Util.DATE_TIME_FORMATTER;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                DATE_TIME_FORMATTER.format(booking.getStart()),
                DATE_TIME_FORMATTER.format(booking.getEnd()),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())
        );
    }

    public static Booking toBooking(BookingDto bookingDto, User booker, Item item, BookingStatus bookingStatus) {
        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), DATE_TIME_FORMATTER);
        return new Booking(
                start,
                end,
                booker,
                item,
                bookingStatus
        );
    }
}
