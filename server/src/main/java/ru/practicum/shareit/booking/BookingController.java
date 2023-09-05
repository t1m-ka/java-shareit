package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto bookItem(@RequestBody BookingDto bookingDto,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId) {
        return service.bookItem(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long ownerId) {
        return service.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfoByBookingId(@PathVariable Long bookingId,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId) {
        return service.getBookingInfoByBookingId(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingUserListByState(@RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        return service.getBookingUserListByState(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingItemsByOwner(@RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long ownerId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        return service.getBookingItemsByOwner(state, ownerId, from, size);
    }
}
