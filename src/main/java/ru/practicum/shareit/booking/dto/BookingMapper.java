package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                formatter.format(booking.getStart()),
                formatter.format(booking.getEnd()),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())
        );
    }

    public static Booking toBooking(BookingDto bookingDto, User booker, Item item, BookingStatus bookingStatus) {
        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), formatter);
        return new Booking(
                start,
                end,
                booker,
                item,
                bookingStatus
        );
    }
}
