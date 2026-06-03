package com.hislink.domain.recruitment.dto;

import com.hislink.domain.community.dto.AuthorSummaryResponse;
import com.hislink.domain.recruitment.entity.RecruitmentComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "팀 모집 댓글/지원 응답")
public class RecruitmentCommentResponse {

    private final Long id;
    private final Long postId;
    private final AuthorSummaryResponse author;
    private final String content;
    private final boolean application;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static RecruitmentCommentResponse from(RecruitmentComment comment) {
        return new RecruitmentCommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                AuthorSummaryResponse.from(comment.getAuthor()),
                comment.getContent(),
                comment.isApplication(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
