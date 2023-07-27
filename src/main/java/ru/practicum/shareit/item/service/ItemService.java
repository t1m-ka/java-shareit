package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item, Long userId);

    Item updateItem(Item item, long itemId, Long userId);

    Item getItemById(long itemId, Long userId);

    List<Item> getOwnerItems(Long userId);

    List<Item> searchItemsByName(String text);
}
