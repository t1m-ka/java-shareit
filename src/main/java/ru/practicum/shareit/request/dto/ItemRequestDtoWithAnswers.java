package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDtoWithAnswers {
    private long id;

    private String description;

    private String created;

    private List<ItemDto> items = new ArrayList<>();

    public ItemRequestDtoWithAnswers(long id, String description, String created, List<ItemDto> items) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.items = items;
    }
}
