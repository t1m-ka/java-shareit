package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Item item, Long userId);

    ItemDto updateItem(Item item, long itemId, Long userId);

    ItemDto getItemById(long itemId, Long userId);

    List<ItemDto> getOwnerItems(Long userId);

    List<ItemDto> searchItemsByName(String text);
}
