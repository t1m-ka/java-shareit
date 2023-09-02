package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.BookingUnavailableException;
import ru.practicum.shareit.util.exception.ItemNotFoundException;
import ru.practicum.shareit.util.exception.OwnershipAccessException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.PageParamsMaker.makePageable;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User user = findUser(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, Long ownerId) {
        User owner = findUser(ownerId);
        Item currentItem = findItem(itemId);
        if (ownerId != currentItem.getOwner().getId())
            throw new OwnershipAccessException("Изменять информацию может только владелец");

        Item newItem = ItemMapper.toItem(itemDto, owner, currentItem.getId());
        if (newItem.getName() != null)
            currentItem.setName(newItem.getName());
        if (newItem.getDescription() != null)
            currentItem.setDescription(newItem.getDescription());
        if (newItem.getAvailable() != null)
            currentItem.setAvailable(newItem.getAvailable());
        if (newItem.getItemRequestId() != null)
            currentItem.setItemRequestId(newItem.getItemRequestId());
        return ItemMapper.toItemDto(itemRepository.save(currentItem));
    }

    @Override
    public ItemDtoWithBookingAndComments getItemById(long itemId, Long userId) {
        User user = findUser(userId);
        Item item = findItem(itemId);
        LocalDateTime now = LocalDateTime.now();
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;
        if (item.getOwner().getId() == user.getId()) {
            lastBooking = findItemLastBooking(itemId, now);
            nextBooking = findItemNextBooking(itemId, now);
        }
        return ItemMapper.toItemDtoWithBookingAndComments(
                item,
                lastBooking,
                nextBooking,
                findAllItemComments(itemId));
    }

    @Override
    public List<ItemDtoWithBookingAndComments> getOwnerItems(Long userId, Integer from, Integer size) {
        Pageable page = makePageable(from, size);
        LocalDateTime now = LocalDateTime.now();
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page).stream()
                .map((Item item) -> ItemMapper.toItemDtoWithBookingAndComments(
                        item,
                        findItemLastBooking(item.getId(), now),
                        findItemNextBooking(item.getId(), now),
                        findAllItemComments(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByName(String text, Integer from, Integer size) {
        if (text.isEmpty())
            return new ArrayList<>();
        Pageable page = makePageable(from, size);
        return itemRepository.findAllByNameAndDescription(text, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User findUser(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Item findItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Вещь с id=" + itemId + " не найдена"));
    }

    private BookingDto findItemLastBooking(long itemId, LocalDateTime now) {
        List<Booking> pastBookingList = bookingRepository
                .findByItemIdAndStartBeforeOrderByEndDesc(itemId, now);
        Optional<Booking> lastBooking = pastBookingList.stream()
                .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                .findFirst();
        return lastBooking.map(BookingMapper::toBookingDto).orElse(null);
    }

    private BookingDto findItemNextBooking(long itemId, LocalDateTime now) {
        List<Booking> futureBookingList = bookingRepository
                .findByItemIdAndStartAfterOrderByStartAsc(itemId, now);
        Optional<Booking> nextBooking = futureBookingList.stream()
                .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                .findFirst();
        return nextBooking.map(BookingMapper::toBookingDto).orElse(null);
    }

    @Override
    public CommentDto addCommentItem(long itemId, Long authorId, CommentDto commentDto) {
        User author = findUser(authorId);
        Item item = findItem(itemId);
        if (!isUserBookItem(item))
            throw new BookingUnavailableException("Вы не можете оставить отзыв");
        Comment newComment = new Comment(
                commentDto.getText(),
                item,
                author,
                LocalDateTime.now()
        );
        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }

    private boolean isUserBookItem(Item item) {
        return findItemLastBooking(item.getId(), LocalDateTime.now()) != null;
    }

    private List<CommentDto> findAllItemComments(long itemId) {
        return commentRepository.findByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
