package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDto {
    private long id;

    private String description;

    private String created;

    public ItemRequestDto(long id, String description, String created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }
}
