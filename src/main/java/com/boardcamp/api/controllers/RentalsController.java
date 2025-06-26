package com.boardcamp.api.controllers;

import com.boardcamp.api.dtos.*;
import com.boardcamp.api.services.RentalsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalsController {

    private final RentalsService service;

    @GetMapping
    public ResponseEntity<List<RentalDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<RentalDTO> create(@Valid @RequestBody RentalRequest body) {
        RentalDTO dto = service.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<RentalDTO> returnRental(@PathVariable Long id) {
        return ResponseEntity.ok(service.returnRental(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
