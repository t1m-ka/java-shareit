package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.util.Util;

import java.time.LocalDateTime;

public class BookingDtoValidator {
    public static boolean validateBookingDto(BookingDto bookingDto) {

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null)
            return false;
        /*LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), Util.DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), Util.DATE_TIME_FORMATTER);*/
        return //bookingDto.getItemId() != null
                bookingDto.getStart() != null
                && bookingDto.getEnd() != null;
    }
}
