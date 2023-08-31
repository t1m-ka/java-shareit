package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-UserDto-Id") long requestorId,
            RequestDto requestDto) {
        log.info("Creating item request {}, requestorId={}", requestDto, requestorId);
        return requestClient.addItemRequest(requestorId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequestList(@RequestHeader("X-Sharer-UserDto-Id") long userId) {
        log.info("Get item request list of user {}", userId);
        return requestClient.getUserItemRequestList(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersItemRequestList(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        return requestClient.getOtherUsersItemRequestList(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long requestId) {
        return requestClient.getItemRequest(userId, requestId);
    }
}
