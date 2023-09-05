package ru.practicum.shareit.item.dto;


import org.apache.commons.lang3.StringUtils;

public class ItemDtoValidator {
    public static boolean validateItemCreation(ItemDto itemDto) {
        return !StringUtils.isBlank(itemDto.getName())
                && !StringUtils.isBlank(itemDto.getDescription())
                && itemDto.getAvailable() != null;
    }

    public static boolean validateItemUpdate(ItemDto itemDto) {
        return !StringUtils.isBlank(itemDto.getName())
                || !StringUtils.isBlank(itemDto.getDescription())
                || itemDto.getAvailable() != null;
    }
}
