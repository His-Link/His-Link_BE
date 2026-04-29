package com.hislink.domain.sample.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class SampleItemCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;
}
