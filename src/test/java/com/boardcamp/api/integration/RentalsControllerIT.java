package com.boardcamp.api.integration;

import com.boardcamp.api.dtos.RentalRequest;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.models.Game;
import com.boardcamp.api.models.Rental;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
class RentalsControllerIT {

        @Autowired
        private TestRestTemplate rest;
        @Autowired
        private RentalRepository rentalRepo;
        @Autowired
        private GameRepository gameRepo;
        @Autowired
        private CustomerRepository custRepo;

        @AfterEach
        void clean() {
                rentalRepo.deleteAll();
                gameRepo.deleteAll();
                custRepo.deleteAll();
        }

        @Test
        void givenWrongGameId_whenCreatingRental_thenNotFound() {
                Customer c = custRepo.save(Customer.builder()
                                .name("Ana").phone("2199").cpf("01234567890").build());

                RentalRequest req = new RentalRequest(c.getId(), 99L, 3);
                ResponseEntity<String> res = rest.exchange("/rentals", HttpMethod.POST,
                                new HttpEntity<>(req), String.class);

                assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
                assertEquals(0, rentalRepo.count());
        }

        @Test
        void givenNoStock_whenCreatingRental_thenUnprocessable() {
                Customer c = custRepo.save(Customer.builder()
                                .name("Ana").phone("2199").cpf("01234567890").build());

                Game g = gameRepo.save(Game.builder()
                                .name("Catan").stockTotal(1).pricePerDay(1500).build());

                rentalRepo.save(Rental.builder()
                                .customer(c).game(g).rentDate(LocalDate.now())
                                .daysRented(2).originalPrice(3000).delayFee(0).build());

                RentalRequest req = new RentalRequest(c.getId(), g.getId(), 2);

                ResponseEntity<String> res = rest.exchange("/rentals", HttpMethod.POST,
                                new HttpEntity<>(req), String.class);

                assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res.getStatusCode());
                assertEquals(1, rentalRepo.count());
        }

        @Test
        void givenValidRental_whenCreatingRental_thenCreated() {
                Customer c = custRepo.save(Customer.builder()
                                .name("Bob").phone("2198").cpf("11122233344").build());

                Game g = gameRepo.save(Game.builder()
                                .name("Azul").stockTotal(2).pricePerDay(2000).build());

                RentalRequest req = new RentalRequest(c.getId(), g.getId(), 2);

                ResponseEntity<Rental> res = rest.exchange("/rentals", HttpMethod.POST,
                                new HttpEntity<>(req), Rental.class);

                assertEquals(HttpStatus.CREATED, res.getStatusCode());
                assertEquals(1, rentalRepo.count());
                assertEquals(4000, res.getBody().getOriginalPrice());
        }

}
