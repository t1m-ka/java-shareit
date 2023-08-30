package ru.practicum.shareit.item.dto;

public class ItemDtoValidator {
    public static boolean validateNewItemDto(ItemDto itemDto) {
        return (itemDto.getName() != null && !itemDto.getName().isBlank())
                && (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
                && itemDto.getAvailable() != null;
    }

    public static boolean validateUpdatedItemDto(ItemDto itemDto) {
        return (itemDto.getName() != null && !itemDto.getName().isBlank())
                || (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
                || itemDto.getAvailable() != null;
    }
}
