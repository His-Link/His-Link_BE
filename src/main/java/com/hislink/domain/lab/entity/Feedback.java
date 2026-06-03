package com.hislink.domain.lab.entity;

import com.hislink.common.entity.BaseTimeEntity;
import com.hislink.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(
        name = "project_feedback",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "author_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private int uiUxScore;

    @Column(nullable = false)
    private int functionalityScore;

    @Column(columnDefinition = "TEXT")
    private String bugReport;

    @Column(nullable = false)
    private int overallSatisfaction;

    @Column(columnDefinition = "TEXT")
    private String opinion;

    @Column(columnDefinition = "TEXT")
    private String improvementSuggestion;

    @Builder
    public Feedback(
            Project project,
            User author,
            int uiUxScore,
            int functionalityScore,
            String bugReport,
            int overallSatisfaction,
            String opinion,
            String improvementSuggestion
    ) {
        this.project = project;
        this.author = author;
        this.uiUxScore = uiUxScore;
        this.functionalityScore = functionalityScore;
        this.bugReport = bugReport;
        this.overallSatisfaction = overallSatisfaction;
        this.opinion = opinion;
        this.improvementSuggestion = improvementSuggestion;
    }

    public void update(
            int uiUxScore,
            int functionalityScore,
            String bugReport,
            int overallSatisfaction,
            String opinion,
            String improvementSuggestion
    ) {
        this.uiUxScore = uiUxScore;
        this.functionalityScore = functionalityScore;
        this.bugReport = bugReport;
        this.overallSatisfaction = overallSatisfaction;
        this.opinion = opinion;
        this.improvementSuggestion = improvementSuggestion;
    }
}
