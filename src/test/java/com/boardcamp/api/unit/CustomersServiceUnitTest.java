package com.boardcamp.api.unit;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.CustomerRequest;
import com.boardcamp.api.exceptions.ConflictException;
import com.boardcamp.api.mappers.BoardcampMapper;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.services.CustomersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomersServiceUnitTest {

    @InjectMocks
    private CustomersService customersService;

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BoardcampMapper mapper;

    @BeforeEach
    void stubMapper() {
        lenient().when(mapper.toCustomer(any(CustomerRequest.class)))
                .thenAnswer(inv -> {
                    CustomerRequest r = inv.getArgument(0);
                    return Customer.builder()
                            .name(r.name())
                            .phone(r.phone())
                            .cpf(r.cpf())
                            .build();
                });

        lenient().when(mapper.toCustomerDTO(any(Customer.class)))
                .thenAnswer(inv -> {
                    Customer c = inv.getArgument(0);
                    return new CustomerDTO(c.getId(), c.getName(),
                            c.getPhone(), c.getCpf());
                });
    }

    @Test
    void givenRepeatedCpf_whenCreatingCustomer_thenThrowsConflict() {
        CustomerRequest req = new CustomerRequest("Ana", "21998887766", "01234567890");
        when(customerRepository.existsByCpf("01234567890")).thenReturn(true);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> customersService.create(req));

        verify(customerRepository).existsByCpf("01234567890");
        verify(customerRepository, never()).save(any());
        assertEquals("CPF already registered", ex.getMessage());
    }

    @Test
    void givenInvalidCpf_whenCreatingCustomer_thenThrowsBadRequest() {
        CustomerRequest req = new CustomerRequest("Ana", "21998887766", "123");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> customersService.create(req));
        assertTrue(ex.getMessage().contains("CPF must be 11 digits"));
    }

    @Test
    void givenValidCustomer_whenCreatingCustomer_thenPersists() {
        CustomerRequest req = new CustomerRequest("Ana", "21998887766", "01234567890");
        Customer baseCustomer = mapper.toCustomer(req);
        Customer saved = new Customer(10L, baseCustomer.getName(), baseCustomer.getPhone(), baseCustomer.getCpf());

        when(customerRepository.existsByCpf("01234567890")).thenReturn(false);
        when(customerRepository.save(any())).thenReturn(saved);

        CustomerDTO dto = customersService.create(req);

        verify(customerRepository).existsByCpf("01234567890");
        verify(customerRepository).save(any(Customer.class));
        assertEquals(10L, dto.id());
        assertEquals("Ana", dto.name());
    }
}
