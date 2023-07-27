package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    //private final

    @Override
    public Item addItem(Item item, long userId) {
        return null;
    }

    @Override
    public Item updateItem(Item item, long itemId, long userId) {
        return null;
    }

    @Override
    public Item getItemById(long itemId, long userId) {
        return null;
    }

    @Override
    public List<Item> getOwnerItems(long userId) {
        return null;
    }

    @Override
    public List<Item> searchItemsByName(String text) {
        return null;
    }
}
