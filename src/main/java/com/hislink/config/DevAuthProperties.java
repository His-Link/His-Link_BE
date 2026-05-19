package com.hislink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.dev")
public class DevAuthProperties {

    /**
     * local 개발 시 true — JWT 없이 API 호출 가능 (Swagger 포함).
     * deploy/test 프로필에서는 반드시 false.
     */
    private boolean authBypass = false;

    /**
     * auth-bypass 시 SecurityContext에 주입할 사용자 이메일.
     * DB에 없으면 자동 생성합니다.
     */
    private String defaultUserEmail = "dev@handong.ac.kr";
}
