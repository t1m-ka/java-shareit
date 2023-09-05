package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDtoWithBookingAndComments {
    long id;

    String name;

    String description;

    Boolean available;

    Long requestId;

    BookingDto lastBooking;

    BookingDto nextBooking;

    List<CommentDto> comments;
}
