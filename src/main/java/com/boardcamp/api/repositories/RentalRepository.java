package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.boardcamp.api.models.Rental;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    long countByGameIdAndReturnDateIsNull(Long gameId);

    @Query("""
            SELECT r FROM Rental r
            JOIN FETCH r.customer
            JOIN FETCH r.game
            """)
    List<Rental> findAllWithDetails();
}
