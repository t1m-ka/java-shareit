package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.PageParamsValidator.validatePageableParams;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addItemRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long requestorId,
            @RequestBody ItemRequestDto itemRequestDto) {
        if (requestorId == null)
            throw new IllegalArgumentException("Отсутствует параметр запроса");
        if (itemRequestDto == null
                || StringUtils.isBlank(itemRequestDto.getDescription()))
            throw new IllegalArgumentException("Отсутствуют обязательные поля");
        return service.addItemRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswers> getUserItemRequestList(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("Отсутствует параметр запроса");
        return service.getUserItemRequestList(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswers> getOtherUsersItemRequestList(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        if (userId == null)
            throw new IllegalArgumentException("Отсутствует параметр запроса");
        if (!validatePageableParams(from, size))
            throw new IllegalArgumentException("Неверно указаны параметры пагинации");
        return service.getOtherUsersItemRequestList(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswers getItemRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long requestId) {
        if (userId == null)
            throw new IllegalArgumentException("Отсутствует параметр запроса");
        return service.getItemRequest(userId, requestId);
    }
}
