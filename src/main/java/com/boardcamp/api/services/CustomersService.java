package com.boardcamp.api.services;

import com.boardcamp.api.dtos.*;
import com.boardcamp.api.exceptions.*;
import com.boardcamp.api.mappers.BoardcampMapper;
import com.boardcamp.api.models.Customer;
import com.boardcamp.api.repositories.CustomerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomersService {

    private final CustomerRepository repo;
    private final BoardcampMapper mapper;

    public List<CustomerDTO> getAll() {
        return mapper.toCustomerDTOs(repo.findAll());
    }

    public CustomerDTO getById(Long id) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        return mapper.toCustomerDTO(customer);
    }

    public CustomerDTO create(CustomerRequest dto) {

        if (dto.cpf().isEmpty())
            throw new IllegalArgumentException("CPF must be 11 digits");
        if (!dto.cpf().matches("\\d{11}"))
            throw new IllegalArgumentException("CPF must be 11 digits");
        if (repo.existsByCpf(dto.cpf()))
            throw new ConflictException("CPF already registered");

        Customer saved = repo.save(mapper.toCustomer(dto));
        return mapper.toCustomerDTO(saved);
    }
}
