package com.boardcamp.api.controllers;

import com.boardcamp.api.dtos.*;
import com.boardcamp.api.services.GamesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GamesController {

    private final GamesService service;

    @GetMapping
    public ResponseEntity<List<GameDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<GameDTO> create(@Valid @RequestBody GameRequest body) {
        GameDTO dto = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
