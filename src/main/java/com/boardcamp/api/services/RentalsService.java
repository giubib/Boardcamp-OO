package com.boardcamp.api.services;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.dtos.RentalRequest;
import com.boardcamp.api.exceptions.NotFoundException;
import com.boardcamp.api.exceptions.UnprocessableEntityException;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.models.Rental;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalsService {

    private final RentalRepository rentals;
    private final GameRepository games;
    private final CustomerRepository customers;

    public List<RentalDTO> getAll() {
        return rentals.findAll()
                .stream()
                .map(r -> RentalDTO.from(r, r.getCustomer(), r.getGame()))
                .toList();
    }

    @Transactional
    public RentalDTO create(RentalRequest dto) {

        Customer customer = customers.findById(dto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        long open = rentals.countByGameIdAndReturnDateIsNull(dto.gameId());

        Game game = games.findById(dto.gameId())
                .orElseThrow(() -> new NotFoundException("Game not found"));

        if (open >= game.getStockTotal())
            throw new UnprocessableEntityException("No stock available for this game");

        int originalPrice = dto.daysRented() * game.getPricePerDay();

        Rental rental = Rental.builder()
                .customer(customer)
                .game(game)
                .rentDate(LocalDate.now())
                .daysRented(dto.daysRented())
                .originalPrice(originalPrice)
                .delayFee(0)
                .build();

        rental = rentals.save(rental);
        return RentalDTO.from(rental, customer, game);
    }

    @Transactional
    public RentalDTO returnRental(Long id) {
        Rental rental = rentals.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found"));

        if (rental.getReturnDate() != null)
            throw new UnprocessableEntityException("Rental already returned");

        LocalDate today = LocalDate.now();
        rental.setReturnDate(today);

        Game game = rental.getGame();

        long diff = ChronoUnit.DAYS.between(
                rental.getRentDate().plusDays(rental.getDaysRented()), today);
        int fee = diff > 0 ? (int) diff * game.getPricePerDay() : 0;

        rental.setDelayFee(fee);
        rental = rentals.save(rental);

        return RentalDTO.from(rental, rental.getCustomer(), game);
    }

    @Transactional
    public void delete(Long id) {
        Rental rental = rentals.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found"));
        if (rental.getReturnDate() == null)
            throw new UnprocessableEntityException("Rental still open");
        rentals.delete(rental);
    }
}
