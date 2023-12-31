package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, Long userId);

    ItemDtoWithBookingAndComments getItemById(long itemId, Long userId);

    List<ItemDtoWithBookingAndComments> getOwnerItems(Long userId, Integer from, Integer size);

    List<ItemDto> searchItemsByName(String text, Integer from, Integer size);

    CommentDto addCommentItem(long itemId, Long authorId, CommentDto commentDto);
}
