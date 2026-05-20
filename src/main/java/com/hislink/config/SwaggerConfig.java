package com.hislink.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HIS-Link API")
                        .version("v1")
                        .description(
                                "한동대 개발자 커뮤니티 HIS-Link 백엔드 API 문서입니다.\n\n"
                                        + "- **Google 로그인:** http://localhost:8080/oauth2/authorization/google\n"
                                        + "- **Swagger 테스트:** Request body 없이 **Parameters** 칸에 값 입력\n"
                                        + "- **로컬(local):** `DEV_AUTH_BYPASS=true` 이면 Authorize 없이 API 호출 가능\n"
                                        + "- **Bearer JWT:** 배포·토큰 테스트 시 Authorize에 access token 입력\n"
                                        + "- 설계 문서: `docs/design/`"
                        )
                        .contact(new Contact().name("HIS-Link Team")))
                .addServersItem(new Server().url("http://localhost:8080").description("Local"))
                .components(new Components()
                        .addSecuritySchemes(OpenApiConstants.BEARER_AUTH, new SecurityScheme()
                                .name(OpenApiConstants.BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Google OAuth 로그인 후 발급받은 access token")));
    }
}
