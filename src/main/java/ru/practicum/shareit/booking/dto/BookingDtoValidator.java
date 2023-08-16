package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.dto.BookingMapper.DATE_TIME_FORMATTER;

public class BookingDtoValidator {
    public static boolean validateBookingDto(BookingDto bookingDto) {

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null)
            return false;
        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), DATE_TIME_FORMATTER);
        return bookingDto.getItemId() != null
                && bookingDto.getStart() != null
                && bookingDto.getEnd() != null
                && start.isBefore(end)
                && start.isAfter(LocalDateTime.now());
    }
}
