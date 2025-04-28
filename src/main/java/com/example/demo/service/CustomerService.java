package com.example.demo.service;

import com.example.demo.dto.CustomerRequestDto;
import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(CustomerRequestDto customerRequestDto) {
        logger.info("Creating a new customer with firstName: {} and lastName: {}", customerRequestDto.getFirstName(), customerRequestDto.getLastName());

        Customer customer = new Customer();
        customer.setFirstName(customerRequestDto.getFirstName());
        customer.setLastName(customerRequestDto.getLastName());
        customer.setDateOfBirth(customerRequestDto.getDateOfBirth());
        customer.setMetadata(customerRequestDto.getMetadata());

        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created with ID: {}", savedCustomer.getId());

        return savedCustomer;
    }

    public Customer getCustomerById(Long id) {
        logger.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id).orElse(null);
    }
}