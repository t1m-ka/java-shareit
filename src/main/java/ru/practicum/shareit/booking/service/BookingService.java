package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto bookItem(BookingDto bookingDto, Long userId);

    BookingDto approveBooking(Long bookingId, boolean approved, Long ownerId);

    BookingDto getBookingInfoByBookingId(Long bookingId, Long userId);

    List<BookingDto> getBookingUserListByState(String state, Long userId, Integer from, Integer size);

    List<BookingDto> getBookingItemsByOwner(String state, Long ownerId, Integer from, Integer size);
}
