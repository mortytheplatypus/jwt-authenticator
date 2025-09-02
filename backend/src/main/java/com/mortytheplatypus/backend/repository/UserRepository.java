package com.mortytheplatypus.backend.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mortytheplatypus.backend.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    @NotNull
    Optional<User> findById(@NotNull UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
