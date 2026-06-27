package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentIncomingDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(CommentIncomingDto dto) {
        if (dto == null) {
            return null;
        }
        return Comment.builder()
                .text(dto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                .created(comment.getCreated())
                .build();
    }
}
