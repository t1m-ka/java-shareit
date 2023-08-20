package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static ru.practicum.shareit.util.Util.DATE_TIME_FORMATTER;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                DATE_TIME_FORMATTER.format(itemRequest.getCreated())
        );
    }

    public static ItemRequestDtoWithAnswers toItemRequestDtoWithAnswers(ItemRequest itemRequest,
            List<ItemDto> itemList) {
        return new ItemRequestDtoWithAnswers(
                itemRequest.getId(),
                itemRequest.getDescription(),
                DATE_TIME_FORMATTER.format(itemRequest.getCreated()),
                itemList
        );
    }
}
