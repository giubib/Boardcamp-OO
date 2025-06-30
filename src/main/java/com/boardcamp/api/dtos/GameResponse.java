package com.boardcamp.api.dtos;

public record GameResponse(
                Long id,
                String name,
                String image,
                Integer stockTotal,
                Integer pricePerDay) {
}
