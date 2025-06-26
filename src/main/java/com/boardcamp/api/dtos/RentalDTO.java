package com.boardcamp.api.dtos;

public record RentalDTO(
        Long id,
        String rentDate,
        Integer daysRented,
        String returnDate,
        Integer originalPrice,
        Integer delayFee,
        CustomerDTO customer,
        GameDTO game) {
}
