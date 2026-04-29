package com.hislink.domain.sample.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "sample_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SampleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public SampleItem(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
