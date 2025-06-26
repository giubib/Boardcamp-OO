package com.boardcamp.api.integration;

import com.boardcamp.api.dtos.GameRequest;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.repositories.GameRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
class GamesControllerIT {

    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private GameRepository repo;

    @AfterEach
    void clean() {
        repo.deleteAll();
    }

    @Test
    void givenRepeatedName_whenCreatingGame_thenConflict() {
        repo.save(Game.builder()
                .name("Catan").image(null).stockTotal(2).pricePerDay(1500).build());

        GameRequest req = new GameRequest("Catan", null, 3, 2000);
        ResponseEntity<String> res = rest.exchange("/games", HttpMethod.POST,
                new HttpEntity<>(req), String.class);

        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertEquals(1, repo.count());
    }

    @Test
    void givenWrongStock_whenCreatingGame_thenBadRequest() {
        GameRequest req = new GameRequest("Azul", null, 0, 1500);

        ResponseEntity<String> res = rest.exchange("/games", HttpMethod.POST,
                new HttpEntity<>(req), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals(0, repo.count());
    }

    @Test
    void givenValidGame_whenCreatingGame_thenCreated() {
        GameRequest req = new GameRequest("Ticket to Ride",
                "http://img", 4, 2500);

        ResponseEntity<Game> res = rest.exchange("/games", HttpMethod.POST,
                new HttpEntity<>(req), Game.class);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals(1, repo.count());
        assertNotNull(res.getBody());
        assertEquals("Ticket to Ride", res.getBody().getName());
    }
}
