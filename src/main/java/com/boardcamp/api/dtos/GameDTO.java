package com.boardcamp.api.dtos;

public record GameDTO(
                Long id,
                String name,
                String image,
                Integer stockTotal,
                Integer pricePerDay) {
}
