package com.hislink.domain.recruitment.entity;

import com.hislink.common.entity.BaseTimeEntity;
import com.hislink.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "recruitment_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruitment_post_id", nullable = false)
    private RecruitmentPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean application;

    @Builder
    public RecruitmentComment(RecruitmentPost post, User author, String content, boolean application) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.application = application;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
