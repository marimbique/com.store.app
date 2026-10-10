package com.store.app.customer.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.app.customer.dto.CustomerRequestDto;
import com.store.app.customer.dto.CustomerResponseDto;
import com.store.app.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping
	public ResponseEntity<CustomerResponseDto> create(@Valid @RequestBody CustomerRequestDto request) {
		CustomerResponseDto customer = customerService.create(request);
		return ResponseEntity.created(URI.create("/api/customers/" + customer.getId())).body(customer);
	}

	@GetMapping
	public List<CustomerResponseDto> findAll() {
		return customerService.findAll();
	}

	@GetMapping("/{id}")
	public CustomerResponseDto findById(@PathVariable Long id) {
		return customerService.findById(id);
	}
}
