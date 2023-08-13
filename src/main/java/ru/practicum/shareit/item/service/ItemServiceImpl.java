package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.OwnershipAccessException;
import ru.practicum.shareit.exception.UserCreateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        if (userId == null)
            throw new UserCreateException("Отсутствует параметр запроса");
        Item item = ItemMapper.toItem(itemDto, userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден")));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден"));
        Item currentItem = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Вещь с id=" + itemId + " не найдена"));
        if (userId != currentItem.getOwner().getId()) {
            throw new OwnershipAccessException("Изменять информацию может только владелец");
        }

        Item newItem = ItemMapper.toItem(itemDto, owner, currentItem.getId());
        if (newItem.getName() != null)
            currentItem.setName(newItem.getName());
        if (newItem.getDescription() != null)
            currentItem.setDescription(newItem.getDescription());
        if (newItem.getAvailable() != null)
            currentItem.setAvailable(newItem.getAvailable());
        if (newItem.getOwner() != null)
            currentItem.setOwner(newItem.getOwner());
        return ItemMapper.toItemDto(itemRepository.save(currentItem));
    }

    @Override
    public ItemDto getItemById(long itemId, Long userId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с id=" + userId + " не найден")));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        return itemRepository.findOwnerItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByName(String text) {
        if (!text.isEmpty())
            return itemRepository.findAllByNameAndDescription(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        return new ArrayList<>();
    }
}
