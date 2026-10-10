package com.store.app.customer.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.store.app.customer.dto.CustomerRequestDto;
import com.store.app.customer.dto.CustomerResponseDto;
import com.store.app.customer.entity.Customer;
import com.store.app.customer.mapper.CustomerMapper;
import com.store.app.customer.repository.CustomerRepository;

@Service
@Transactional
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;

	public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
		this.customerRepository = customerRepository;
		this.customerMapper = customerMapper;
	}

	public CustomerResponseDto create(CustomerRequestDto request) {
		if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer code already exists");
		}
		if (customerRepository.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
		}

		Customer customer = customerRepository.save(customerMapper.toEntity(request));
		return customerMapper.toResponse(customer);
	}

	@Transactional(readOnly = true)
	public List<CustomerResponseDto> findAll() {
		return customerRepository.findAll().stream()
				.map(customerMapper::toResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CustomerResponseDto findById(Long id) {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
		return customerMapper.toResponse(customer);
	}
}
