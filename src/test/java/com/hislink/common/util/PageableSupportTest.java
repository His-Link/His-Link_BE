package com.hislink.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PageableSupportTest {

    @Test
    @DisplayName("허용되지 않은 sort 필드는 기본 정렬로 대체한다")
    void sanitize_invalidSortProperty() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "string"));

        Pageable sanitized = PageableSupport.sanitize(
                pageable,
                50,
                Set.of("createdAt", "viewCount"),
                "createdAt",
                Sort.Direction.DESC
        );

        assertThat(sanitized.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
    }
}
