package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item, long userId);

    Item updateItem(Item item, long userId);

    Item getItem(long itemId);

    List<Item> getItemsListByOwner(long userId);

    List<Item> searchItem(String text);
}
