package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GameRequest(
        @NotBlank String name,
        String image,
        @NotNull @Positive Integer stockTotal,
        @NotNull @Positive Integer pricePerDay) {
}
