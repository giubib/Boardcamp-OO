package com.boardcamp.api.services;

import com.boardcamp.api.dtos.*;
import com.boardcamp.api.exceptions.*;
import com.boardcamp.api.mappers.BoardcampMapper;
import com.boardcamp.api.models.*;
import com.boardcamp.api.repositories.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalsService {

    private final RentalRepository rentalRepo;
    private final GameRepository gameRepo;
    private final CustomerRepository customerRepo;
    private final BoardcampMapper mapper;

    public List<RentalDTO> getAll() {
        return mapper.toRentalDTOs(rentalRepo.findAllWithDetails());
    }

    @Transactional
    public RentalDTO create(RentalRequest dto) {
        Customer customer = customerRepo.findById(dto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        Game game = gameRepo.findById(dto.gameId())
                .orElseThrow(() -> new NotFoundException("Game not found"));

        int openRentals = rentalRepo.countByGameIdAndReturnDateIsNull(game.getId());
        if (openRentals >= game.getStockTotal())
            throw new UnprocessableEntityException("No stock available for this game");

        LocalDate today = LocalDate.now();
        int originalFee = game.getPricePerDay() * dto.daysRented();

        Rental rental = Rental.builder()
                .customer(customer)
                .game(game)
                .rentDate(today)
                .daysRented(dto.daysRented())
                .originalPrice(originalFee)
                .delayFee(0)
                .returnDate(null)
                .build();

        return mapper.toRentalDTO(rentalRepo.save(rental));
    }

    @Transactional
    public RentalDTO returnRental(Long id) {
        Rental rental = rentalRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found"));

        if (rental.getReturnDate() != null)
            throw new UnprocessableEntityException("Rental already finalized");

        LocalDate today = LocalDate.now();
        rental.setReturnDate(today);

        LocalDate expectedReturn = rental.getRentDate().plusDays(rental.getDaysRented());
        long daysLate = ChronoUnit.DAYS.between(expectedReturn, today);
        if (daysLate < 0)
            daysLate = 0;

        int delayFee = (int) (daysLate * rental.getGame().getPricePerDay());
        rental.setDelayFee(delayFee);

        return mapper.toRentalDTO(rentalRepo.save(rental));
    }

    @Transactional
    public void delete(Long id) {
        Rental rental = rentalRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found"));

        if (rental.getReturnDate() == null)
            throw new BadRequestException("Cannot delete rental still in progress");

        rentalRepo.delete(rental);
    }
}
