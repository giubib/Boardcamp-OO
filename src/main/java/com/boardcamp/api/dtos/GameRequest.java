package com.boardcamp.api.dtos;

import jakarta.validation.constraints.*;

public record GameRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 255) String image,
        @NotNull @Positive Integer stockTotal,
        @NotNull @Positive Integer pricePerDay) {
}
