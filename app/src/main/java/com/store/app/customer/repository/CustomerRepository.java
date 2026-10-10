package com.store.app.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.app.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByCustomerCode(String customerCode);

	boolean existsByEmail(String email);
}
