package com.hislink.domain.lab.entity;

import com.hislink.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "project_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "user_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ProjectLike(Project project, User user) {
        this.project = project;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
