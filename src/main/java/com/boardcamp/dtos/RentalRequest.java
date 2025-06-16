package com.boardcamp.dtos;

import jakarta.validation.constraints.*;

public record RentalRequest(
        @NotNull Long customerId,
        @NotNull Long gameId,
        @Positive Integer daysRented
) {}
