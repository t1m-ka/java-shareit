package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemDtoValidator.validateNewItemDto;
import static ru.practicum.shareit.item.dto.ItemDtoValidator.validateUpdatedItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null)
            throw new ArgumentNotFoundException("Отсутствует параметр запроса");
        if (!validateNewItemDto(itemDto))
            throw new ArgumentNotFoundException("Отсутствуют обязательные поля");
        return service.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestBody ItemDto itemDto,
            @PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        if (ownerId == null)
            throw new ArgumentNotFoundException("Отсутствует параметр запроса");
        if (!validateUpdatedItemDto(itemDto))
            throw new ArgumentNotFoundException("Отсутствуют обязательные поля");
        return service.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComments getItemById(@PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null)
            throw new ArgumentNotFoundException("Отсутствует параметр запроса");
        return service.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookingAndComments> getOwnerItems(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId) {
        if (ownerId == null)
            throw new ArgumentNotFoundException("Отсутствует параметр запроса");
        return service.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByName(@RequestParam("text") String text,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null)
            throw new ArgumentNotFoundException("Отсутствует параметр запроса");
        return service.searchItemsByName(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long authorId,
            @RequestBody CommentDto commentDto) {
        if (authorId == null)
            throw new ArgumentNotFoundException("Отсутствует параметр запроса");
        if (commentDto.getText() == null || commentDto.getText().isBlank())
            throw new ArgumentNotFoundException("Отсутствуют обязательные поля");
        return service.addCommentItem(itemId, authorId, commentDto);
    }
}
