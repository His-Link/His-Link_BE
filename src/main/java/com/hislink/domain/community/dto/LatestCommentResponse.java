package com.hislink.domain.community.dto;

import com.hislink.domain.community.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "최신 댓글 (사이드바용)")
public class LatestCommentResponse {

    private final Long id;
    private final Long postId;
    private final String postTitle;
    private final AuthorSummaryResponse author;
    private final String contentPreview;
    private final LocalDateTime createdAt;

    public static LatestCommentResponse from(Comment comment) {
        return new LatestCommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getPost().getTitle(),
                AuthorSummaryResponse.from(comment.getAuthor()),
                toPreview(comment.getContent()),
                comment.getCreatedAt()
        );
    }

    private static String toPreview(String content) {
        if (content == null) {
            return "";
        }
        if (content.length() <= 60) {
            return content;
        }
        return content.substring(0, 60) + "...";
    }
}
