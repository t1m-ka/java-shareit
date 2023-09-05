package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithAnswers> getUserItemRequestList(Long userId);

    List<ItemRequestDtoWithAnswers> getOtherUsersItemRequestList(Long userId, Integer from, Integer size);

    ItemRequestDtoWithAnswers getItemRequest(Long userId, Long requestId);
}
