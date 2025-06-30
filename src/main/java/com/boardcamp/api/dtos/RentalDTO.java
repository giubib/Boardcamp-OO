package com.boardcamp.api.dtos;

import com.boardcamp.api.models.Customer;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.models.Rental;

import java.time.LocalDate;

public record RentalDTO(Long id,
                LocalDate rentDate,
                Integer daysRented,
                LocalDate returnDate,
                Integer originalPrice,
                Integer delayFee,
                CustomerDTO customer,
                GameDTO game) {

        public static RentalDTO from(Rental r, Customer c, Game g) {
                return new RentalDTO(
                                r.getId(),
                                r.getRentDate(),
                                r.getDaysRented(),
                                r.getReturnDate(),
                                r.getOriginalPrice(),
                                r.getDelayFee(),
                                CustomerDTO.from(c),
                                GameDTO.from(g));
        }
}
