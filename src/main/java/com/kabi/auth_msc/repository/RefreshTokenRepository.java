package com.kabi.auth_msc.repository;

import com.kabi.auth_msc.entity.RefreshToken;
import com.kabi.auth_msc.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    public Optional<RefreshToken> findByRefreshToken(String refreshToken);

    public Optional<RefreshToken> findByUser(User user);

    public void deleteByRefreshToken(String refresgToken);
}
