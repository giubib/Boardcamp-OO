package com.boardcamp.api.unit;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.GameRequest;
import com.boardcamp.api.exceptions.ConflictException;
import com.boardcamp.api.mappers.BoardcampMapper;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.services.GamesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamesServiceUnitTest {

    @InjectMocks
    private GamesService gamesService;

    @Mock
    private GameRepository gameRepository;
    @Mock
    private BoardcampMapper mapper;

    @BeforeEach
    void stubMapper() {
        lenient().when(mapper.toGame(any(GameRequest.class)))
                .thenAnswer(inv -> {
                    GameRequest r = inv.getArgument(0);
                    return Game.builder()
                            .name(r.name())
                            .image(r.image())
                            .stockTotal(r.stockTotal())
                            .pricePerDay(r.pricePerDay())
                            .build();
                });

        lenient().when(mapper.toGameDTO(any(Game.class)))
                .thenAnswer(inv -> {
                    Game g = inv.getArgument(0);
                    return new GameDTO(g.getId(),
                            g.getName(),
                            g.getImage(),
                            g.getStockTotal(),
                            g.getPricePerDay());
                });
    }

    @Test
    void givenRepeatedName_whenCreatingGame_thenThrowsConflict() {
        GameRequest req = new GameRequest("Catan", null, 2, 1500);

        when(gameRepository.existsByNameIgnoreCase("catan")).thenReturn(true);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> gamesService.create(req));

        verify(gameRepository).existsByNameIgnoreCase("catan");
        verify(gameRepository, never()).save(any());
        assertEquals("Game already exists", ex.getMessage());
    }

    @Test
    void givenInvalidStock_whenCreatingGame_thenThrowsBadRequest() {
        GameRequest req = new GameRequest("Ticket to Ride", null, 0, 1500);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gamesService.create(req));
        assertTrue(ex.getMessage().contains("stock must be > 0"));
    }

    @Test
    void givenValidGame_whenCreatingGame_thenPersists() {
        GameRequest req = new GameRequest("Azul", "http://img", 3, 2000);

        Game toSave = mapper.toGame(req);
        Game saved = new Game(1L, toSave.getName(), toSave.getImage(), toSave.getStockTotal(), toSave.getPricePerDay());

        when(gameRepository.existsByNameIgnoreCase("azul")).thenReturn(false);
        when(gameRepository.save(any())).thenReturn(saved);

        GameDTO dto = gamesService.create(req);

        verify(gameRepository).existsByNameIgnoreCase("azul");
        verify(gameRepository).save(any(Game.class));
        assertEquals(1L, dto.id());
        assertEquals("Azul", dto.name());
    }
}
