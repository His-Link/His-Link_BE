package com.hislink.domain.recruitment.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "recruitment_post_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruitment_post_id", nullable = false)
    private RecruitmentPost post;

    @Column(nullable = false, length = 255)
    private String storedFileName;

    @Column(nullable = false)
    private int sortOrder;

    @Builder
    public RecruitmentPostImage(RecruitmentPost post, String storedFileName, int sortOrder) {
        this.post = post;
        this.storedFileName = storedFileName;
        this.sortOrder = sortOrder;
    }

    void assignPost(RecruitmentPost post) {
        this.post = post;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
