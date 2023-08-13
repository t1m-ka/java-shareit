package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingSearchStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto bookItem(BookingDto bookingDto, Long userId) {
        User booker = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ItemNotFoundException("Вещь с id=" + bookingDto.getItemId() + " не найдена"));
        if (booker.getId() == item.getId())
            throw new OwnershipAccessException("Владелец не может бронировать свои вещи");
        Booking newBooking = BookingMapper.toBooking(bookingDto, booker, item, BookingStatus.WAITING);
        if (!item.getAvailable())
            throw new BookingUnavailableException("Бронирование запрещено владельцем");
        if (!bookingRepository.findItemBookingBetweenDate(item.getId(), newBooking.getStart(), newBooking.getEnd())
                .isEmpty())
            throw new BookingUnavailableException("На указанное время бронирование невозможно");

        return BookingMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Override
    public BookingDto approveBooking(Long bookingId, boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        userRepository.findById(ownerId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + ownerId + " не найден"));
        if (booking.getItem().getOwner().getId() != ownerId)
            throw new OwnershipAccessException("Смена статуса разрешена только владельцу");

        if ((booking.getStatus() == BookingStatus.APPROVED && approved)
                || (booking.getStatus() == BookingStatus.REJECTED && !approved))
            throw new BookingStatusException("Статус уже изменен");
        if (approved)
            booking.setStatus(BookingStatus.APPROVED);
        else
            booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingInfoByBookingId(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();
        if (userId != bookerId && userId != ownerId)
            throw new OwnershipAccessException("Недостаточно прав для просмотра информации");
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingUserListByState(String state, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));
        BookingSearchStatus bookingSearchStatus;
        try {
            bookingSearchStatus = BookingSearchStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingStatusException("Unknown state: " + state);
        }
        switch (bookingSearchStatus.name()) {
            case "ALL":
                return bookingRepository.findAllByBookerId(
                                userId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartIsAfter(
                                userId,
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByBookerIdAndEndIsBefore(
                                userId,
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllCurrentBookingUser(LocalDateTime.now(), userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllBookingUserByState(
                                BookingSearchStatus.toBookingStatus(bookingSearchStatus), userId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingDto> getBookingItemsByOwner(String state, Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + ownerId + " не найден"));
        BookingSearchStatus bookingSearchStatus;
        try {
            bookingSearchStatus = BookingSearchStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BookingStatusException("Unknown state: " + state);
        }
        switch (bookingSearchStatus.name()) {
            case "ALL":
                return bookingRepository.findByItemOwnerId(
                                ownerId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByItemOwnerIdAndStartIsAfter(
                                ownerId,
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByItemOwnerIdAndEndIsBefore(
                                ownerId,
                                LocalDateTime.now(),
                                Sort.by(Sort.Direction.DESC, "end"))
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAllCurrentBookingItemsByOwner(
                                LocalDateTime.now(),
                                ownerId)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllBookingItemsByOwner(
                                BookingSearchStatus.toBookingStatus(bookingSearchStatus),
                                ownerId)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }
}
