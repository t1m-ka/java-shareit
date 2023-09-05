package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addItemRequest(
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long requestorId,
            @RequestBody ItemRequestDto itemRequestDto) {
        return service.addItemRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswers> getUserItemRequestList(
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId) {
        return service.getUserItemRequestList(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswers> getOtherUsersItemRequestList(
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        return service.getOtherUsersItemRequestList(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswers getItemRequest(
            @RequestHeader(value = "X-Sharer-UserDto-Id", required = false) Long userId,
            @PathVariable Long requestId) {
        return service.getItemRequest(userId, requestId);
    }
}
