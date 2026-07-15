package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperUnitTest {

    @Test
    void testNullInputs() {
        assertThat(CommentMapper.toComment(null)).isNull();
        assertThat(CommentMapper.toCommentResponseDto(null)).isNull();
    }

    @Test
    void testToCommentResponseDto_whenAuthorIsNull() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Тестовый комментарий");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(null);

        CommentResponseDto dto = CommentMapper.toCommentResponseDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getAuthorName()).isNull();
    }

    @Test
    void testToCommentResponseDto_withAuthor() {
        User author = new User();
        author.setName("Алексей");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Тестовый комментарий");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);

        CommentResponseDto dto = CommentMapper.toCommentResponseDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getAuthorName()).isEqualTo("Алексей");
    }
}
