package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoWithComments {
    long id;

    String name;

    String description;

    Boolean available;

    Long requestId;

    List<CommentDto> comments;
}