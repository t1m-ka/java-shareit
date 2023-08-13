package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingDtoValidator {
    public static boolean validateBookingDto(BookingDto bookingDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null)
            return false;
        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), formatter);
        return bookingDto.getItemId() != null
                && bookingDto.getStart() != null
                && bookingDto.getEnd() != null
                && start.isBefore(end)
                && start.isAfter(LocalDateTime.now());
    }
}
