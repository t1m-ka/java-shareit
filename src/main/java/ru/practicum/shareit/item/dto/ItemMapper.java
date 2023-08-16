package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item,
            BookingDto lastBooking,
            BookingDto nextBooking) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking
        );
    }

    public static ItemDtoWithComments toItemDtoWithComments(
            Item item,
            List<CommentDto> comments) {
        return new ItemDtoWithComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                comments
        );
    }

    public static ItemDtoWithBookingAndComments toItemDtoWithBookingAndComments(
            Item item,
            BookingDto lastBooking,
            BookingDto nextBooking,
            List<CommentDto> comments) {
        return new ItemDtoWithBookingAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner
        );
    }

    public static Item toItem(ItemDto itemDto, User owner, long itemId) {
        return new Item(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner
        );
    }
}
