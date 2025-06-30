package com.boardcamp.api.dtos;

import com.boardcamp.api.models.Customer;

public record CustomerDTO(Long id, String name, String phone, String cpf) {

    public static CustomerDTO from(Customer c) {
        if (c == null)
            return null;
        return new CustomerDTO(c.getId(), c.getName(), c.getPhone(), c.getCpf());
    }
}
