package com.hislink.domain.lab.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "project_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 255)
    private String storedFileName;

    @Column(nullable = false)
    private int sortOrder;

    @Builder
    public ProjectImage(Project project, String storedFileName, int sortOrder) {
        this.project = project;
        this.storedFileName = storedFileName;
        this.sortOrder = sortOrder;
    }

    void assignProject(Project project) {
        this.project = project;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
