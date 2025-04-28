package com.example.demo.controller;

import com.example.demo.dto.CustomerRequestDto;
import com.example.demo.exception.DataNotFoundException;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@Validated
public class CustomerDataController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDataController.class);

    private final CustomerService customerService;

    public CustomerDataController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody @Valid CustomerRequestDto customerRequestDto) {
        try {
            logger.info("Request received to create customer with data: {}", customerRequestDto);

            Customer customer = customerService.createCustomer(customerRequestDto);

            if (customer == null) {
                logger.error("Failed to create customer, service returned null.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            logger.info("Customer created successfully with ID: {}", customer.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while creating customer: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        try {
            logger.info("Request received to fetch customer with ID: {}", id);
            Customer customer = customerService.getCustomerById(id);
            if (customer==null) {
                throw new DataNotFoundException("Customer not found with ID " + id);
            }
            return ResponseEntity.ok(customer);
        } catch (DataNotFoundException ex) {
            logger.error("Data not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            logger.error("Unexpected error occurred: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}