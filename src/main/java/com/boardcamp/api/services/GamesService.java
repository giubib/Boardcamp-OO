package com.boardcamp.api.services;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.GameRequest;
import com.boardcamp.api.exceptions.ConflictException;
import com.boardcamp.api.mappers.BoardcampMapper;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.repositories.GameRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamesService {

    private final GameRepository gameRepository;
    private final BoardcampMapper mapper;

    public List<GameDTO> getAll() {
        return mapper.toGameDTOs(gameRepository.findAll());
    }

    public GameDTO create(GameRequest dto) {
        if (dto.stockTotal() <= 0) {
            throw new IllegalArgumentException("stock must be > 0");
        }
        if (dto.pricePerDay() <= 0) {
            throw new IllegalArgumentException("pricePerDay must be > 0");
        }
        String normalizedName = dto.name().toLowerCase();
        if (gameRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ConflictException("Game already exists");
        }
        Game saved = gameRepository.save(mapper.toGame(dto));
        return mapper.toGameDTO(saved);
    }
}
