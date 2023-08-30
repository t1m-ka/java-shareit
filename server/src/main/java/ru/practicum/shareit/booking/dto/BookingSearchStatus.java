package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.util.exception.BookingStatusException;

public enum BookingSearchStatus {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    WAITING,
    APPROVED,
    REJECTED;

    public static BookingStatus toBookingStatus(BookingSearchStatus bookingSearchStatus) {
        switch (bookingSearchStatus) {
            case WAITING:
                return BookingStatus.WAITING;
            case APPROVED:
                return BookingStatus.APPROVED;
            case REJECTED:
                return BookingStatus.REJECTED;
            default:
                throw new BookingStatusException("Статус бронирования не может быть: " + bookingSearchStatus);
        }
    }
}
