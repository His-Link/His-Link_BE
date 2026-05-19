package com.hislink.domain.auth.repository;

import com.hislink.domain.auth.entity.RefreshToken;
import com.hislink.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByUser(User user);

    void deleteByTokenHash(String tokenHash);
}
