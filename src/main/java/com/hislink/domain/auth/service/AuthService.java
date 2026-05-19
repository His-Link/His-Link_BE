package com.hislink.domain.auth.service;

import com.hislink.common.exception.AuthException;
import com.hislink.common.util.JwtTokenProvider;
import com.hislink.common.util.TokenHashUtil;
import com.hislink.common.util.TokenType;
import com.hislink.domain.auth.dto.TokenResponse;
import com.hislink.domain.auth.entity.RefreshToken;
import com.hislink.domain.auth.repository.RefreshTokenRepository;
import com.hislink.domain.auth.validator.EmailDomainValidator;
import com.hislink.domain.user.entity.Role;
import com.hislink.domain.user.entity.User;
import com.hislink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailDomainValidator emailDomainValidator;

    @Value("${security.jwt.access-token-expire-ms}")
    private long accessTokenExpireMs;

    @Value("${security.jwt.refresh-token-expire-ms}")
    private long refreshTokenExpireMs;

    @Transactional
    public User upsertGoogleUser(String email, String name, String profileImageUrl, String googleSub) {
        if (!emailDomainValidator.isAllowed(email)) {
            throw new AuthException("Only Handong University email domains are allowed.");
        }

        return userRepository.findByGoogleSub(googleSub)
                .map(user -> {
                    user.updateProfile(name, profileImageUrl);
                    return user;
                })
                .orElseGet(() -> userRepository.findByEmail(email)
                        .map(user -> {
                            user.updateProfile(name, profileImageUrl);
                            return user;
                        })
                        .orElseGet(() -> userRepository.save(User.builder()
                                .email(email)
                                .name(name)
                                .profileImageUrl(profileImageUrl)
                                .googleSub(googleSub)
                                .role(Role.USER)
                                .build())));
    }

    @Transactional
    public TokenResponse issueTokens(User user) {
        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole());

        saveRefreshToken(user, refreshToken);

        return new TokenResponse(accessToken, refreshToken, "Bearer", accessTokenExpireMs);
    }

    @Transactional
    public TokenResponse refreshTokens(String refreshTokenValue) {
        if (!jwtTokenProvider.validateToken(refreshTokenValue, TokenType.REFRESH)) {
            throw new AuthException("Invalid refresh token.");
        }

        String tokenHash = TokenHashUtil.hash(refreshTokenValue);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new AuthException("Refresh token not found."));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new AuthException("Refresh token has expired.");
        }

        User user = storedToken.getUser();
        refreshTokenRepository.delete(storedToken);
        return issueTokens(user);
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        if (!jwtTokenProvider.validateToken(refreshTokenValue, TokenType.REFRESH)) {
            return;
        }
        refreshTokenRepository.deleteByTokenHash(TokenHashUtil.hash(refreshTokenValue));
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found."));
    }

    @Transactional(readOnly = true)
    public User getUserFromAccessToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken, TokenType.ACCESS)) {
            throw new AuthException("Invalid access token.");
        }
        Long userId = jwtTokenProvider.getUserId(accessToken);
        return getUser(userId);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpireMs / 1000);
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(TokenHashUtil.hash(refreshToken))
                .expiresAt(expiresAt)
                .build());
    }
}
