package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@NoArgsConstructor
public class BookingDto {
    private long id;

    private Long itemId;

    private String start;

    private String end;

    private BookingStatus status;

    private UserDto booker;

    private ItemDto item;

    public BookingDto(long id, long itemId, String start, String end, BookingStatus status, UserDto booker, ItemDto item) {
        this.id = id;
        this.itemId = itemId;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }
}
