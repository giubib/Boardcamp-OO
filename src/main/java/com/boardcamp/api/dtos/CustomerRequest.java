package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequest(
                @NotBlank String name,
                @Pattern(regexp = "\\d{10,11}") String phone,
                @Pattern(regexp = "\\d{11}") String cpf) {
}
