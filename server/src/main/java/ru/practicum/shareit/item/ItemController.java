package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId) {
        log.info("Creating item {}, ownerId={}", itemDto, userId);
        return service.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestBody ItemDto itemDto,
            @PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long ownerId) {
        log.info("Updating item {}, ownerId={}, itemId={}", itemDto, ownerId, itemId);
        return service.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComments getItemById(@PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookingAndComments> getOwnerItems(
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long ownerId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        log.info("Get all items of owner {}, pageable from={}, size={}", ownerId, from, size);
        return service.getOwnerItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByName(@RequestParam("text") String text,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        log.info("Search items by text={} from user {}, pageable from={}, size={}", text, userId, from, size);
        return service.searchItemsByName(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long authorId,
            @RequestBody CommentDto commentDto) {
        log.info("Add comment to item {}, from user {}", itemId, authorId);
        return service.addCommentItem(itemId, authorId, commentDto);
    }
}
