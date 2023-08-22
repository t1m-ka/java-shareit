package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    long id;

    String name;

    String description;

    Boolean available;

    Long requestId;
}
