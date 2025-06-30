package com.boardcamp.api.services;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.CustomerRequest;
import com.boardcamp.api.exceptions.ConflictException;
import com.boardcamp.api.exceptions.NotFoundException;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomersService {

    private final CustomerRepository repo;

    public List<CustomerDTO> getAll() {
        return repo.findAll().stream()
                .map(CustomerDTO::from)
                .toList();
    }

    public CustomerDTO getById(Long id) {
        Customer c = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        return CustomerDTO.from(c);
    }

    @Transactional
    public CustomerDTO create(CustomerRequest dto) {

        /* validações de domínio exigidas pelos testes */
        if (dto.name() == null || dto.name().isBlank())
            throw new IllegalArgumentException("Name cannot be blank");

        if (dto.phone() == null || !dto.phone().matches("\\d{10,11}"))
            throw new IllegalArgumentException("Phone must have 10 or 11 digits");

        if (dto.cpf() == null || !dto.cpf().matches("\\d{11}"))
            throw new IllegalArgumentException("CPF must be 11 digits");

        if (repo.existsByCpf(dto.cpf()))
            throw new ConflictException("CPF already registered");

        Customer c = Customer.builder()
                .name(dto.name())
                .phone(dto.phone())
                .cpf(dto.cpf())
                .build();

        c = repo.save(c);
        return CustomerDTO.from(c);
    }
}
