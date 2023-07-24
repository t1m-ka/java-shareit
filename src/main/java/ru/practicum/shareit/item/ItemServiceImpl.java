package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Override
    public Item addItem(Item item, long userId) {
        return null;
    }

    @Override
    public Item updateItem(Item item, long userId) {
        return null;
    }

    @Override
    public Item getItem(long itemId) {
        return null;
    } // userid?

    @Override
    public List<Item> getItemsListByOwner(long userId) {
        return null;
    }

    @Override
    public List<Item> searchItem(String text) {
        return null;
    }
}
