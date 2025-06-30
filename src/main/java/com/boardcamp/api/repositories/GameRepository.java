package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardcamp.api.models.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByNameIgnoreCase(String name);
}