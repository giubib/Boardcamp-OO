package com.boardcamp.api.services;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.GameRequest;
import com.boardcamp.api.exceptions.ConflictException;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.repositories.GameRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamesService {

    private final GameRepository repo;

    public List<GameDTO> getAll() {
        return repo.findAll().stream()
                .map(GameDTO::from)
                .toList();
    }

    @Transactional
    public GameDTO create(GameRequest dto) {

        if (dto.name() == null || dto.name().isBlank())
            throw new IllegalArgumentException("Name cannot be blank");
        if (dto.stockTotal() <= 0)
            throw new IllegalArgumentException("stock must be > 0");

        if (dto.pricePerDay() <= 0)
            throw new IllegalArgumentException("pricePerDay must be > 0");

        String normalized = dto.name().toLowerCase();
        if (repo.existsByNameIgnoreCase(normalized))
            throw new ConflictException("Game already exists");

        Game game = Game.builder()
                .name(dto.name())
                .image(dto.image())
                .stockTotal(dto.stockTotal())
                .pricePerDay(dto.pricePerDay())
                .build();

        game = repo.save(game);
        return GameDTO.from(game);
    }
}
