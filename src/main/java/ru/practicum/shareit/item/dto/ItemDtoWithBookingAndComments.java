package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoWithBookingAndComments {
    long id;

    String name;

    String description;

    Boolean available;

    BookingDto lastBooking;

    BookingDto nextBooking;

    List<CommentDto> comments;
}
