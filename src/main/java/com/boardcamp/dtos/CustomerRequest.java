package com.boardcamp.dtos;

import jakarta.validation.constraints.*;

public record CustomerRequest(
        @NotBlank @Size(max = 100)        String name,
        @Pattern(regexp = "^[0-9]{10,11}$") String phone,
        @Pattern(regexp = "^[0-9]{11}$")   String cpf
) {}
