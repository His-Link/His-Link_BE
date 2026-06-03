package com.hislink.domain.recruitment.entity;

import com.hislink.common.entity.BaseTimeEntity;
import com.hislink.domain.techstack.entity.TechStack;
import com.hislink.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(name = "recruitment_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Convert(converter = RecruitmentActivityTypeConverter.class)
    @Column(name = "activity_type", nullable = false, length = 30)
    private RecruitmentActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "recruitment_role", nullable = false, length = 30)
    private RecruitmentRole recruitmentRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecruitmentStatus status;

    @Column(nullable = false)
    private int participantLimit;

    @Column(nullable = false)
    private int currentCount;

    private LocalDateTime deadline;

    @Column(length = 200)
    private String contactMethod;

    @Column(length = 500)
    private String thumbnailUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "recruitment_tech_stack",
            joinColumns = @JoinColumn(name = "recruitment_post_id"),
            inverseJoinColumns = @JoinColumn(name = "tech_stack_id")
    )
    private final Set<TechStack> techStacks = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private final List<RecruitmentPostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<RecruitmentComment> comments = new ArrayList<>();

    @Builder
    public RecruitmentPost(
            User author,
            String title,
            String description,
            RecruitmentActivityType activityType,
            RecruitmentRole recruitmentRole,
            RecruitmentStatus status,
            int participantLimit,
            LocalDateTime deadline,
            String contactMethod
    ) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.activityType = activityType != null ? activityType : RecruitmentActivityType.PROJECT;
        this.recruitmentRole = recruitmentRole;
        this.status = status != null ? status : RecruitmentStatus.OPEN;
        this.participantLimit = participantLimit;
        this.currentCount = 0;
        this.deadline = deadline;
        this.contactMethod = contactMethod;
    }

    public void update(
            String title,
            String description,
            RecruitmentActivityType activityType,
            RecruitmentRole recruitmentRole,
            RecruitmentStatus status,
            int participantLimit,
            LocalDateTime deadline,
            String contactMethod
    ) {
        this.title = title;
        this.description = description;
        this.activityType = activityType != null ? activityType : RecruitmentActivityType.PROJECT;
        this.recruitmentRole = recruitmentRole;
        this.status = status;
        this.participantLimit = participantLimit;
        this.deadline = deadline;
        this.contactMethod = contactMethod;
    }

    public void replaceTechStacks(Set<TechStack> stacks) {
        this.techStacks.clear();
        this.techStacks.addAll(stacks);
    }

    public void addImage(RecruitmentPostImage image) {
        image.assignPost(this);
        this.images.add(image);
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void increaseCurrentCount() {
        this.currentCount++;
    }

    public void decreaseCurrentCount() {
        if (this.currentCount > 0) {
            this.currentCount--;
        }
    }

    public boolean isFull() {
        return currentCount >= participantLimit;
    }
}
