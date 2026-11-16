package com.example.projectdnd.repository;

import com.example.projectdnd.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for managing {@link User} entities in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their unique account username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the matching {@link User}, or empty if no user exists with that username
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks whether a user with the specified username already exists.
     *
     * @param username the username to check
     * @return {@code true} if a user with the given username exists, {@code false} otherwise
     */
    Boolean existsByUsername(String username);
}

