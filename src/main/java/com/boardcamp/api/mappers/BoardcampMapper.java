package com.boardcamp.api.mappers;

import com.boardcamp.api.dtos.*;
import com.boardcamp.api.models.*;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BoardcampMapper {

    public Game toGame(GameRequest dto) {
        if (dto == null)
            return null;
        return Game.builder()
                .name(dto.name())
                .image(dto.image())
                .stockTotal(dto.stockTotal())
                .pricePerDay(dto.pricePerDay())
                .build();
    }

    public GameDTO toGameDTO(Game entity) {
        if (entity == null)
            return null;
        return new GameDTO(
                entity.getId(),
                entity.getName(),
                entity.getImage(),
                entity.getStockTotal(),
                entity.getPricePerDay());
    }

    public GameResponse toGameResponse(Game entity) {
        if (entity == null)
            return null;
        return new GameResponse(
                entity.getId(),
                entity.getName(),
                entity.getImage(),
                entity.getStockTotal(),
                entity.getPricePerDay());
    }

    public List<GameDTO> toGameDTOs(List<Game> entities) {
        return entities == null ? List.of()
                : entities.stream().map(this::toGameDTO).toList();
    }

    public List<GameResponse> toGameResponses(List<Game> entities) {
        return entities == null ? List.of()
                : entities.stream().map(this::toGameResponse).toList();
    }

    public Customer toCustomer(CustomerRequest dto) {
        if (dto == null)
            return null;
        return Customer.builder()
                .name(dto.name())
                .phone(dto.phone())
                .cpf(dto.cpf())
                .build();
    }

    public CustomerDTO toCustomerDTO(Customer entity) {
        if (entity == null)
            return null;
        return new CustomerDTO(
                entity.getId(),
                entity.getName(),
                entity.getPhone(),
                entity.getCpf());
    }

    public List<CustomerDTO> toCustomerDTOs(List<Customer> entities) {
        return entities == null ? List.of()
                : entities.stream().map(this::toCustomerDTO).toList();
    }

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    public RentalDTO toRentalDTO(Rental entity) {
        if (entity == null)
            return null;
        return new RentalDTO(
                entity.getId(),
                entity.getRentDate() != null ? entity.getRentDate().format(ISO) : null,
                entity.getDaysRented(),
                entity.getReturnDate() != null ? entity.getReturnDate().format(ISO) : null,
                entity.getOriginalPrice(),
                entity.getDelayFee(),
                toCustomerDTO(entity.getCustomer()),
                toGameDTO(entity.getGame()));
    }

    public List<RentalDTO> toRentalDTOs(List<Rental> entities) {
        return entities == null ? List.of()
                : entities.stream().map(this::toRentalDTO).toList();
    }

    public static Rental newRental(Customer customer, Game game, RentalRequest req) {
        return Rental.builder()
                .customer(customer)
                .game(game)
                .daysRented(req.daysRented())
                .build();
    }
}
