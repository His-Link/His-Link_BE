package com.hislink.config;

import com.hislink.common.security.DevAuthBypassFilter;
import com.hislink.common.security.JwtAuthenticationFilter;
import com.hislink.domain.auth.handler.OAuth2AuthenticationFailureHandler;
import com.hislink.domain.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.hislink.domain.auth.security.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_GET_PATHS = {
            "/api/auth/google",
            "/oauth2/**",
            "/login/oauth2/**",
            "/api/community/posts",
            "/api/community/posts/**",
            "/api/community/comments/latest",
            "/api/lab/projects",
            "/api/lab/projects/**",
            "/api/tech-stacks",
            "/api/main/dashboard",
            "/api/recruitment/posts",
            "/api/recruitment/posts/**",
            "/uploads/**"
    };

    private static final String[] PUBLIC_POST_PATHS = {
            "/api/auth/refresh",
            "/api/auth/logout"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final DevAuthBypassFilter devAuthBypassFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, PUBLIC_GET_PATHS).permitAll()
                .antMatchers(HttpMethod.POST, PUBLIC_POST_PATHS).permitAll()
                .antMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/health",
                        "/error"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(devAuthBypassFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
