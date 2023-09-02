package ru.practicum.shareit.request;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.PageParamsValidator.validatePageableParams;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
            @RequestBody RequestDto requestDto) {
        if (requestorId == null)
            throw new IllegalArgumentException("Request header is missing");
        if (requestDto == null
                || StringUtils.isBlank(requestDto.getDescription()))
            throw new IllegalArgumentException("Item description is missing");
        log.info("Creating item request {}, requestorId={}", requestDto, requestorId);
        return requestClient.addItemRequest(requestorId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequestList(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("Request header is missing");
        log.info("Get item request list of user {}", userId);
        return requestClient.getUserItemRequestList(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersItemRequestList(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (userId == null)
            throw new IllegalArgumentException("Request header is missing");
        if (!validatePageableParams(from, size))
            throw new IllegalArgumentException("Pagination parameters are incorrect");
        return requestClient.getOtherUsersItemRequestList(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long requestId) {
        if (userId == null)
            throw new IllegalArgumentException("Request header is missing");
        return requestClient.getItemRequest(userId, requestId);
    }
}
