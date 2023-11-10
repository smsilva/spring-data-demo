package com.github.smsilva.example.spring.data.boundary;

import com.github.smsilva.example.spring.data.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}
