package com.hislink.domain.techstack.dto;

import com.hislink.domain.techstack.entity.TechStack;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "기술 스택")
public class TechStackResponse {

    private final Long id;
    private final String name;

    public static TechStackResponse from(TechStack techStack) {
        return new TechStackResponse(techStack.getId(), techStack.getName());
    }
}
