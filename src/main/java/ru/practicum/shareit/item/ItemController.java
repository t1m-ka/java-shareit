package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestBody @Valid Item item,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return service.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody Item item, @PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return service.updateItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return service.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByName(@RequestParam("text") String text,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return service.searchItemsByName(text);
    }
}
