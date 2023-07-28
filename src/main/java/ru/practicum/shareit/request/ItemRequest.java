package ru.practicum.shareit.request;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
@Data
public class ItemRequest {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
