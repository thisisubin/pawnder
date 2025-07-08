package com.pawnder.repository;

import com.pawnder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

}
