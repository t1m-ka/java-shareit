package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public Optional<Item> addItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> updateItem(Item item, long itemId) {
        Item updatedItem = items.get(itemId);
        if (item.getName() != null && !item.getName().isBlank())
            updatedItem.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            updatedItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            updatedItem.setAvailable(item.getAvailable());
        return Optional.of(updatedItem);
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return items.containsKey(itemId) ? Optional.of(items.get(itemId)) : Optional.empty();
    }

    @Override
    public List<Item> getOwnerItems(long userId) {
        return items.values().stream()
                .filter(x -> x.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemsByName(String text) {
        return items.values().stream()
                .filter(x -> (x.getName().toUpperCase().contains(text.toUpperCase())
                        || x.getDescription().toUpperCase().contains(text.toUpperCase()))
                        && x.getAvailable())
                .collect(Collectors.toList());
    }
}
