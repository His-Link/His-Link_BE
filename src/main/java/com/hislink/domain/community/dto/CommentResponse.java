package com.hislink.domain.community.dto;

import com.hislink.domain.community.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "댓글 응답")
public class CommentResponse {

    private final Long id;
    private final Long postId;
    private final AuthorSummaryResponse author;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                AuthorSummaryResponse.from(comment.getAuthor()),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
