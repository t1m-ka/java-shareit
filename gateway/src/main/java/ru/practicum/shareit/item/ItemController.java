package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.dto.ItemDtoValidator.validateItemCreation;
import static ru.practicum.shareit.item.dto.ItemDtoValidator.validateItemUpdate;
import static ru.practicum.shareit.util.PageParamsValidator.validatePageableParams;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        if (ownerId == null)
            throw new IllegalArgumentException("Request header is missing");
        if (!validateItemCreation(itemDto))
            throw new IllegalArgumentException("Data validation error");
        log.info("Creating item {}, ownerId={}", itemDto, ownerId);
        return itemClient.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestBody ItemDto itemDto,
            @PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        if (ownerId == null)
            throw new IllegalArgumentException("Request header is missing");
        if (!validateItemUpdate(itemDto))
            throw new IllegalArgumentException("Item data validation error");
        log.info("Updating item {}, ownerId={}, itemId={}", itemDto, ownerId, itemId);
        return itemClient.updateItem(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("Request header is missing");
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (ownerId == null)
            throw new IllegalArgumentException("Request header is missing");
        if (!validatePageableParams(from, size))
            throw new IllegalArgumentException("Pagination parameters are incorrect");
        log.info("Get all items of owner {}, pageable from={}, size={}", ownerId, from, size);
        return itemClient.getOwnerItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByName(@RequestParam("text") String text,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (userId == null)
            throw new IllegalArgumentException("Request header is missing");
        if (!validatePageableParams(from, size))
            throw new IllegalArgumentException("Pagination parameters are incorrect");
        log.info("Search items by text={} from user {}, pageable from={}, size={}", text, userId, from, size);
        return itemClient.searchItemsByName(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long authorId,
            @RequestBody CommentDto commentDto) {
        if (authorId == null)
            throw new IllegalArgumentException("Request header is missing");
        if (StringUtils.isBlank(commentDto.getText()))
            throw new IllegalArgumentException("Comment text is missing");
        log.info("Add comment to item {}, from user {}", itemId, authorId);
        return itemClient.addCommentToItem(itemId, authorId, commentDto);
    }
}
