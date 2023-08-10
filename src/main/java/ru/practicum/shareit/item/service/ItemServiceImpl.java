package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    public ItemDto addItem(Item item, Long userId) {
        //checkUserExist(userId);
        //item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Item item, long itemId, Long userId) {
        /*checkUserExist(userId);
        Item updatedItem = itemRepository.getItemById(itemId).get();
        if (updatedItem == null)
            throw new UserNotFoundException("Пользователь с id=" + userId + "не найден");
        if (updatedItem.getOwner() == userId)
            return ItemMapper.toItemDto(itemRepository.updateItem(item, itemId).get());
        else
            throw new ItemOwnershipException("Изменять информацию может только владелец");*/
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(long itemId, Long userId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId).get());
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

    /*private void checkUserExist(Long userId) {
        if (userId == null)
            throw new ArgumentNotFoundException("Идентификатор пользователя не указан");
        if (userRepository.getUserById(userId).isEmpty())
            throw new UserNotFoundException("Пользователя не существует");
    }*/
}
