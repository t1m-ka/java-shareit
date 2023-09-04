package ru.practicum.shareit.booking.dto;

public class BookingDtoValidator {
    public static boolean validateBookingDto(BookingDto bookingDto) {
        return bookingDto.getStart() != null
                && bookingDto.getEnd() != null
                && !bookingDto.getStart().isAfter(bookingDto.getEnd())
                && !bookingDto.getStart().isEqual(bookingDto.getEnd());
    }
}