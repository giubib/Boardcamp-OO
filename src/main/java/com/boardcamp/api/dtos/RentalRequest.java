package com.boardcamp.api.dtos;

import jakarta.validation.constraints.*;

public record RentalRequest(
        @NotNull Long customerId,
        @NotNull Long gameId,
        @NotNull @Positive Integer daysRented) {
}
