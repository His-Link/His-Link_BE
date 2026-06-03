package com.hislink.domain.lab.entity;

import com.hislink.common.entity.BaseTimeEntity;
import com.hislink.domain.techstack.entity.TechStack;
import com.hislink.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(length = 500)
    private String serviceUrl;

    @Column(length = 500)
    private String githubUrl;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String testRequest;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int feedbackCount;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgUiUxScore;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgFunctionalityScore;

    @Column(precision = 3, scale = 2)
    private BigDecimal avgOverallScore;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_tech_stack",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "tech_stack_id")
    )
    private final Set<TechStack> techStacks = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private final List<ProjectImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProjectLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Feedback> feedbacks = new ArrayList<>();

    @Builder
    public Project(
            User author,
            String title,
            String summary,
            String serviceUrl,
            String githubUrl,
            String testRequest
    ) {
        this.author = author;
        this.title = title;
        this.summary = summary;
        this.serviceUrl = serviceUrl;
        this.githubUrl = githubUrl;
        this.testRequest = testRequest;
        this.viewCount = 0;
        this.likeCount = 0;
        this.feedbackCount = 0;
    }

    public void update(
            String title,
            String summary,
            String serviceUrl,
            String githubUrl,
            String testRequest
    ) {
        this.title = title;
        this.summary = summary;
        this.serviceUrl = serviceUrl;
        this.githubUrl = githubUrl;
        this.testRequest = testRequest;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void addImage(ProjectImage image) {
        image.assignProject(this);
        this.images.add(image);
    }

    public void replaceTechStacks(Set<TechStack> stacks) {
        this.techStacks.clear();
        this.techStacks.addAll(stacks);
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void applyFeedbackAggregates(
            int feedbackCount,
            BigDecimal avgUiUxScore,
            BigDecimal avgFunctionalityScore,
            BigDecimal avgOverallScore
    ) {
        this.feedbackCount = feedbackCount;
        this.avgUiUxScore = avgUiUxScore;
        this.avgFunctionalityScore = avgFunctionalityScore;
        this.avgOverallScore = avgOverallScore;
    }

    public static BigDecimal averageScore(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return null;
        }
        double avg = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }
}
