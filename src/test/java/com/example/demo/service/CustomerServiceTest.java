package com.example.demo.service;

import com.example.demo.dto.CustomerRequestDto;
import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequestDto customerRequestDto;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRequestDto = CustomerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .metadata("Sample metadata")
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .createdOn(LocalDateTime.now())
                .metadata("Sample metadata")
                .build();
    }

    @Test
    void testCreateCustomer_Success() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer createdCustomer = customerService.createCustomer(customerRequestDto);

        assertNotNull(createdCustomer);
        assertEquals("John", createdCustomer.getFirstName());
        assertEquals("Doe", createdCustomer.getLastName());
        assertEquals(1L, createdCustomer.getId());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testGetCustomerById_Found() {
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(customer));

        Customer foundCustomer = customerService.getCustomerById(1L);

        assertNotNull(foundCustomer);
        assertEquals("John", foundCustomer.getFirstName());
        assertEquals("Doe", foundCustomer.getLastName());
        assertEquals(1L, foundCustomer.getId());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertNull(customerService.getCustomerById(1L));
        verify(customerRepository, times(1)).findById(1L);
    }
}
