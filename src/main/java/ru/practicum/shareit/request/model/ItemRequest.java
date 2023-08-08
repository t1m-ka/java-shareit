package ru.practicum.shareit.request.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class ItemRequest {

    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
