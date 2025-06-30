package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RentalRequest(
        @NotNull Long customerId,
        @NotNull Long gameId,
        @NotNull @Positive Integer daysRented) {
}
