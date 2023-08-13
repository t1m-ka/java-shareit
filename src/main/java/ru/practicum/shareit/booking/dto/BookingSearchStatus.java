package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingStatusException;

public enum BookingSearchStatus {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    WAITING,
    APPROVED;

    public static BookingStatus toBookingStatus(BookingSearchStatus bookingSearchStatus) {
        switch (bookingSearchStatus) {
            case WAITING:
                return BookingStatus.WAITING;
            case APPROVED:
                return BookingStatus.APPROVED;
            default:
                throw new BookingStatusException("Статус бронирования не может быть: " + bookingSearchStatus);
        }
    }
}
