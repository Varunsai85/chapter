package org.varun.chapterbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.varun.chapterbackend.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByUsernameOrEmail(String username, String email);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUsername(String username);
}
