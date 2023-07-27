package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public Item addItem(@RequestBody @Valid Item item, @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody Item item, @PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.updateItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public List<Item> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItemsByName(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam("text") String text) {
        return service.searchItemsByName(text);
    }
}
