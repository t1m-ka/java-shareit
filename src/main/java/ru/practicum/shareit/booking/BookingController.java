package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.booking.dto.BookingDtoValidator.validateBookingDto;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto bookItem(@RequestBody BookingDto bookingDto,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (!validateBookingDto(bookingDto))
            throw new IllegalArgumentException("Ошибка входных данных");
        if (userId == null)
            throw new IllegalArgumentException("Отсутсвтует обязательный заголовок запроса");
        return service.bookItem(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        return service.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfoByBookingId(@PathVariable Long bookingId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return service.getBookingInfoByBookingId(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingUserListByState(@RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return service.getBookingUserListByState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingItemsByOwner(@RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        return service.getBookingItemsByOwner(state, ownerId);
    }
}
