package com.boardcamp.dtos;

public record CustomerDTO(
        Long id,
        String name,
        String phone,
        String cpf) {
}
