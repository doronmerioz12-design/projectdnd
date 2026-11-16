package com.example.projectdnd.repository;

import com.example.projectdnd.model.entity.Spell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for managing {@link Spell} entities.
 */
@Repository
public interface SpellRepository extends JpaRepository<Spell, Long> {
    /**
     * Retrieves a spell by its unique name.
     *
     * @param name the name of the spell to look up
     * @return an {@link Optional} containing the matching {@link Spell}, or empty if not found
     */
    Optional<Spell> findByName(String name);
}