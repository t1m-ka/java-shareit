package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.booking.dto.BookingDtoValidator.validateBookingDto;
import static ru.practicum.shareit.util.PageParamsValidator.validatePageableParams;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public BookingDto bookItem(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingDto requestDto) {
        if (userId == null)
            throw new IllegalArgumentException("Request header is missing");
        if (!validateBookingDto(requestDto))
            throw new IllegalArgumentException("Booking data validation error");
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        if (ownerId == null)
            throw new IllegalArgumentException("Request header is missing");
        log.info("Approving booking {}={}, ownerId={}", bookingId, approved, ownerId);
        return bookingClient.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        if (userId == null)
            throw new IllegalArgumentException("Request header is missing");
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        if (!validatePageableParams(from, size))
            throw new IllegalArgumentException("Pagination parameters are incorrect");
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingItemsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        if (!validatePageableParams(from, size))
            throw new IllegalArgumentException("Pagination parameters are incorrect");
        log.info("Get booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookingItemsByOwner(ownerId, state, from, size);
    }
}
