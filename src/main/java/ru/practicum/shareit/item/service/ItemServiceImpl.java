package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ArgumentNotFoundException;
import ru.practicum.shareit.exception.ItemOwnershipException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item addItem(Item item, Long userId) {
        checkUserExist(userId);
        item.setOwner(userId);
        return itemRepository.addItem(item).get();
    }

    @Override
    public Item updateItem(Item item, long itemId, Long userId) {
        checkUserExist(userId);
        Item updatedItem = itemRepository.getItemById(itemId).get();
        if (updatedItem == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + "не найден");
        if (updatedItem.getOwner() == userId)
            return itemRepository.updateItem(item, itemId).get();
        else
            throw new ItemOwnershipException("Изменять информацию может только владелец");
    }

    @Override
    public Item getItemById(long itemId, Long userId) {
        return itemRepository.getItemById(itemId).get();
    }

    @Override
    public List<Item> getOwnerItems(Long userId) {
        return itemRepository.getOwnerItems(userId);
    }

    @Override
    public List<Item> searchItemsByName(String text) {
        if (!text.isEmpty())
            return itemRepository.searchItemsByName(text);
        return new ArrayList<>();
    }

    private void checkUserExist(Long userId) {
        if (userId == null)
            throw new ArgumentNotFoundException("Идентификатор пользователя не указан");
        if (userRepository.getUserById(userId).isEmpty())
            throw new UserNotFoundException("Пользователя не существует");
    }
}
