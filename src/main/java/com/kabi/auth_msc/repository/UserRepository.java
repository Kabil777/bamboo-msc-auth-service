package com.kabi.auth_msc.repository;

import com.kabi.auth_msc.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByEmail(String email);

    public Optional<User> findByName(String name);
}
