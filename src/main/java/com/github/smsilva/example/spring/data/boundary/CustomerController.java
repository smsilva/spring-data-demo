package com.github.smsilva.example.spring.data.boundary;

import java.util.List;

import com.github.smsilva.example.spring.data.entity.Customer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/customers")
    public List<Customer> getAll() {
        return repository.findAll();
    }

}
