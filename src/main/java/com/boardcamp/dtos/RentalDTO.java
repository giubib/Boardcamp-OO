package com.boardcamp.dtos;

public record RentalDTO(
        Long id,
        String rentDate, // formato ISO: 2025-06-20
        Integer daysRented,
        String returnDate, // null se não devolvido
        Integer originalPrice,
        Integer delayFee,
        CustomerDTO customer, // objeto aninhado já mapeado
        GameDTO game) {
}
