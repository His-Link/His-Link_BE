package com.hislink.domain.sample.dto;

import com.hislink.domain.sample.entity.SampleItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SampleItemResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    public static SampleItemResponse from(SampleItem sampleItem) {
        return SampleItemResponse.builder()
                .id(sampleItem.getId())
                .title(sampleItem.getTitle())
                .description(sampleItem.getDescription())
                .createdAt(sampleItem.getCreatedAt())
                .build();
    }
}
