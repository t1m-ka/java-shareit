package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.time.format.DateTimeFormatter;

public class CommentMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                formatter.format(comment.getCreated())
        );
    }
}
