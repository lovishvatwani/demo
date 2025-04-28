package com.example.demo.controller;

import com.example.demo.dto.CustomerRequestDto;
import com.example.demo.exception.DataNotFoundException;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerDataController.class)
class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_Success() throws Exception {
        CustomerRequestDto requestDto = CustomerRequestDto.builder()
                .firstName("Alice")
                .lastName("Wonderland")
                .dateOfBirth(LocalDate.of(1999, 12, 31))
                .metadata("Test user")
                .build();

        Customer createdCustomer = Customer.builder()
                .id(1L)
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .dateOfBirth(requestDto.getDateOfBirth())
                .createdOn(LocalDateTime.now())
                .metadata(requestDto.getMetadata())
                .build();

        when(customerService.createCustomer(any(CustomerRequestDto.class))).thenReturn(createdCustomer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Wonderland"));
    }

    @Test
    void getCustomerById_Success() throws Exception {
        Customer customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .createdOn(LocalDateTime.now())
                .metadata("Regular Customer")
                .build();

        when(customerService.getCustomerById(1L)).thenReturn(customer);

        mockMvc.perform(get("/customers/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getCustomerById_NotFound() throws Exception {
        when(customerService.getCustomerById(99L)).thenThrow(new DataNotFoundException("Customer not found"));

        mockMvc.perform(get("/customers/{id}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCustomer_ValidationFail() throws Exception {
        CustomerRequestDto invalidRequest = CustomerRequestDto.builder()
                .firstName("")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .metadata("Invalid Request")
                .build();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
