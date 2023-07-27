package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemOwnershipException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Override
    public Item addItem(Item item, long userId) {
        item.setOwner(userId);
        return repository.addItem(item).get();
    }

    @Override
    public Item updateItem(Item item, long itemId, long userId) {
        Item updatedItem = repository.getItemById(itemId).get();
        if (updatedItem == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + "не найден");
        if (updatedItem.getOwner() == userId)
            return repository.updateItem(item, itemId).get();
        else
            throw new ItemOwnershipException("Изменять информацию может только владелец");
    }

    @Override
    public Item getItemById(long itemId, long userId) {
        return repository.getItemById(itemId).get();
    }

    @Override
    public List<Item> getOwnerItems(long userId) {
        return repository.getOwnerItems(userId);
    }

    @Override
    public List<Item> searchItemsByName(String text) {
        return repository.searchItemsByName(text);
    }
}
