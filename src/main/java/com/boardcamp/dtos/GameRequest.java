package com.boardcamp.dtos;

import jakarta.validation.constraints.*;

public record GameRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 255) String image,
        @Positive                      Integer stockTotal,
        @Positive                      Integer pricePerDay
) {}
