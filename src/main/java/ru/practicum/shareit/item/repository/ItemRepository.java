package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> addItem(Item item);

    Optional<Item> updateItem(Item item, long itemId);

    Optional<Item> getItemById(long itemId);

    List<Item> getOwnerItems(long userId);

    List<Item> searchItemsByName(String text);
}
