package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, Long userId);

    ItemDto getItemById(long itemId, Long userId);

    List<ItemDto> getOwnerItems(Long userId);

    List<ItemDto> searchItemsByName(String text);
}
