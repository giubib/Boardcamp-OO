package com.boardcamp.api.integration;

import com.boardcamp.api.dtos.CustomerRequest;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.repositories.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.test.context.ActiveProfiles("test")
class CustomersControllerTest {

        @Autowired
        private TestRestTemplate rest;
        @Autowired
        private CustomerRepository repo;

        @AfterEach
        void clean() {
                repo.deleteAll();
        }

        @Test
        void givenRepeatedCpf_whenCreatingCustomer_thenConflict() {
                repo.save(Customer.builder()
                                .name("Ana").phone("21999999999")
                                .cpf("11122233344").build());

                CustomerRequest req = new CustomerRequest(
                                "Outra Ana", "21988888888", "11122233344");

                ResponseEntity<String> res = rest.exchange("/customers", HttpMethod.POST,
                                new HttpEntity<>(req), String.class);

                assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
                assertEquals(1, repo.count());
        }

        @Test
        void givenWrongPhone_whenCreatingCustomer_thenBadRequest() {
                CustomerRequest req = new CustomerRequest(
                                "Bob", "telefone", "12345678901");

                ResponseEntity<String> res = rest.exchange("/customers", HttpMethod.POST,
                                new HttpEntity<>(req), String.class);

                assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
                assertEquals(0, repo.count());
        }

        @Test
        void givenValidCustomer_whenCreatingCustomer_thenCreated() {
                CustomerRequest req = new CustomerRequest(
                                "Clara", "21911112222", "98765432100");

                ResponseEntity<Customer> res = rest.exchange("/customers", HttpMethod.POST,
                                new HttpEntity<>(req), Customer.class);

                assertEquals(HttpStatus.CREATED, res.getStatusCode());
                assertEquals(1, repo.count());
                assertEquals("Clara", res.getBody().getName());
        }
}
