package com.boardcamp.api.unit;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.dtos.RentalRequest;
import com.boardcamp.api.exceptions.NotFoundException;
import com.boardcamp.api.exceptions.UnprocessableEntityException;
import com.boardcamp.api.mappers.BoardcampMapper;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.models.Rental;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;
import com.boardcamp.api.services.RentalsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalsServiceUnitTest {

        @InjectMocks
        private RentalsService rentalsService;

        @Mock
        private RentalRepository rentalRepository;
        @Mock
        private GameRepository gameRepository;
        @Mock
        private CustomerRepository customerRepository;
        @Mock
        private BoardcampMapper mapper;

        private final Game game = Game.builder()
                        .id(1L).name("Catan")
                        .stockTotal(2).pricePerDay(1500)
                        .build();

        private final Customer customer = Customer.builder()
                        .id(1L).name("Ana")
                        .phone("2199").cpf("01234567890")
                        .build();

        @BeforeEach
        void stubMapper() {
                lenient().when(mapper.toCustomerDTO(any(Customer.class)))
                                .thenAnswer(inv -> {
                                        Customer c = inv.getArgument(0);
                                        return new CustomerDTO(c.getId(), c.getName(),
                                                        c.getPhone(), c.getCpf());
                                });

                lenient().when(mapper.toGameDTO(any(Game.class)))
                                .thenAnswer(inv -> {
                                        Game g = inv.getArgument(0);
                                        return new GameDTO(g.getId(), g.getName(),
                                                        g.getImage(), g.getStockTotal(),
                                                        g.getPricePerDay());
                                });

                lenient().when(mapper.toRentalDTO(any(Rental.class)))
                                .thenAnswer(inv -> {
                                        Rental r = inv.getArgument(0);
                                        return new RentalDTO(
                                                        r.getId(),
                                                        r.getRentDate().toString(),
                                                        r.getDaysRented(),
                                                        r.getReturnDate() == null ? null
                                                                        : r.getReturnDate().toString(),
                                                        r.getOriginalPrice(),
                                                        r.getDelayFee(),
                                                        mapper.toCustomerDTO(r.getCustomer()),
                                                        mapper.toGameDTO(r.getGame()));
                                });
        }

        @Test
        void givenWrongGameId_whenCreatingRental_thenNotFound() {
                RentalRequest req = new RentalRequest(1L, 99L, 3);

                when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
                when(gameRepository.findById(99L)).thenReturn(Optional.empty());

                NotFoundException ex = assertThrows(
                                NotFoundException.class,
                                () -> rentalsService.create(req));

                verify(gameRepository).findById(99L);
                assertEquals("Game not found", ex.getMessage());
        }

        @Test
        void givenNoStock_whenCreatingRental_thenUnprocessable() {
                RentalRequest req = new RentalRequest(1L, 1L, 3);

                when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
                when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
                when(rentalRepository.countByGameIdAndReturnDateIsNull(1L))
                                .thenReturn(game.getStockTotal());

                UnprocessableEntityException ex = assertThrows(
                                UnprocessableEntityException.class,
                                () -> rentalsService.create(req));

                verify(rentalRepository).countByGameIdAndReturnDateIsNull(1L);
                assertEquals("No stock available for this game", ex.getMessage());
        }

        @Test
        void givenValidRental_whenCreatingRental_thenPersists() {
                RentalRequest req = new RentalRequest(1L, 1L, 2);

                when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
                when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
                when(rentalRepository.countByGameIdAndReturnDateIsNull(1L)).thenReturn(0);

                Rental toSave = Rental.builder()
                                .customer(customer)
                                .game(game)
                                .rentDate(LocalDate.now())
                                .daysRented(2)
                                .originalPrice(game.getPricePerDay() * 2)
                                .delayFee(0)
                                .build();

                Rental saved = Rental.builder()
                                .id(50L)
                                .customer(toSave.getCustomer())
                                .game(toSave.getGame())
                                .rentDate(toSave.getRentDate())
                                .daysRented(toSave.getDaysRented())
                                .originalPrice(toSave.getOriginalPrice())
                                .delayFee(toSave.getDelayFee())
                                .returnDate(toSave.getReturnDate())
                                .build();

                when(rentalRepository.save(any())).thenReturn(saved);

                RentalDTO dto = rentalsService.create(req);

                verify(rentalRepository).save(any(Rental.class));
                assertEquals(50L, dto.id());
                assertEquals(0, dto.delayFee());
        }
}
