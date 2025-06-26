package com.boardcamp.api.dtos;

import jakarta.validation.constraints.*;

public record CustomerRequest(
                @NotBlank @Size(max = 100) String name,
                @NotNull @Pattern(regexp = "^\\d{10,11}$") String phone,
                @NotNull @Pattern(regexp = "^\\d{11}$") String cpf) {
}
