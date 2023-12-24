package com.github.smsilva.example.spring.data;

import com.github.smsilva.example.spring.data.boundary.CustomerRepository;
import com.github.smsilva.example.spring.data.entity.Customer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.*;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
public class IntegrationTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTests.class);

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.4-alpine3.18");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        LOGGER.info("Setting create properties");
        LOGGER.info("JDBC URL: {}", postgres.getJdbcUrl());
        LOGGER.info("Username: {}", postgres.getUsername());
        LOGGER.info("Password: {}", postgres.getPassword());

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        customerRepository.deleteAll();
    }

    @Test
    void shouldGetAllCustomers() {
        customerRepository.saveAll(generateCustomerList(BigInteger.valueOf(250)));

        Customer[] customers = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/customers")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(Customer[].class);

        assertThat(customers.length).isEqualTo(250);
    }

    @NotNull
    private static List<Customer> generateCustomerList(BigInteger count) {
        List<Customer> customers = new ArrayList<>();

        for (int id = 1; id <= count.intValue(); id++) {
            String name = "customer." + id;
            String email = name + "@mail.com";

            LOGGER.info("Creating customer with id: {}, name: {}, email: {}", id, name, email);

            Customer customer = new Customer((long) id, name, email);

            customers.add(customer);
        }

        return customers;
    }

}
