package com.boardcamp.api.dtos;

import com.boardcamp.api.models.Game;

public record GameDTO(Long id,
        String name,
        String image,
        Integer stockTotal,
        Integer pricePerDay) {

    public static GameDTO from(Game g) {
        if (g == null)
            return null;
        return new GameDTO(
                g.getId(),
                g.getName(),
                g.getImage(),
                g.getStockTotal(),
                g.getPricePerDay());
    }
}
